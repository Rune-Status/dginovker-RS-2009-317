package org.gielinor.game.node.entity.combat;

import java.util.ArrayList;
import java.util.List;

import org.gielinor.cache.def.impl.NPCDefinition;
import org.gielinor.game.content.skill.free.magic.MagicSpell;
import org.gielinor.game.node.Node;
import org.gielinor.game.node.entity.Entity;
import org.gielinor.game.node.entity.combat.equipment.SpellType;
import org.gielinor.game.node.entity.impl.Animator.Priority;
import org.gielinor.game.node.entity.impl.Projectile;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.entity.player.link.SpellBookManager.SpellBook;
import org.gielinor.game.node.entity.player.link.audio.Audio;
import org.gielinor.game.node.item.Item;
import org.gielinor.game.world.World;
import org.gielinor.game.world.map.RegionManager;
import org.gielinor.game.world.update.flag.context.Animation;
import org.gielinor.game.world.update.flag.context.Graphics;
import org.gielinor.rs2.pulse.Pulse;

/**
 * Represents a combat magic spell.
 *
 * @author Emperor
 */
public abstract class CombatSpell extends MagicSpell {

    /**
     * The autocast animation.
     */
    public static final Animation AUTOCAST_ANIMATION = new Animation(1162, Priority.HIGH);

    /**
     * The splash graphics.
     */
    public static final Graphics SPLASH_GRAPHIC = new Graphics(85, 96);

    /**
     * The current spell type.
     */
    protected final SpellType type;

    /**
     * The projectile.
     */
    protected Projectile projectile;

    /**
     * The end graphic.
     */
    protected final Graphics endGraphic;

    /**
     * The component id.
     */
    private int componentId;

    /**
     * The impact sound.
     */
    protected final int impactSound;

    /**
     * Constructs a new {@code CombatSpell} {@Code Object}
     */
    public CombatSpell() {
        this(SpellType.NULL, SpellBook.MODERN, 1, 0.0, -1, -1, null, null, null, null);
    }

    /**
     * Constructs a new {@code CombatSpell} {@Code Object}
     *
     * @param type           The spell type.
     * @param book           The spell book.
     * @param level          The level required.
     * @param baseExperience The base experience.
     * @param castSound      The sound id of the casting sound.
     * @param impactSound    The sound id of the impact sound.
     * @param animation      the cast animation.
     * @param startGraphic   The start graphic.
     * @param projectile     The projectile.
     * @param endGraphic     The end graphic.
     * @param runes          The runes required to cast this spell.
     */
    public CombatSpell(SpellType type, SpellBook book, int level, double baseExperience, int castSound, int impactSound, Animation animation, Graphics startGraphic, Projectile projectile, Graphics endGraphic, Item... runes) {
        super(book, level, baseExperience, animation, startGraphic, new Audio(castSound, 1, 0), runes);
        this.type = type;
        this.impactSound = impactSound;
        this.projectile = projectile;
        this.endGraphic = endGraphic;
    }

    /**
     * Gets the maximum impact amount of this spell.
     *
     * @param entity The entity.
     * @param victim The victim.
     * @param state  The battle state.
     * @return The maximum impact amount.
     */
    public abstract int getMaximumImpact(Entity entity, Entity victim, BattleState state);

    /**
     * Starts the effect of this spell (if any).
     *
     * @param entity The entity.
     * @param victim The victim.
     * @param state  The battle state.
     */
    public void fireEffect(Entity entity, Entity victim, BattleState state) {

    }

    /**
     * Gets a list of possible targets for a multihitting spell.
     *
     * @param entity The caster of the spell.
     * @param target The victim.
     * @param max    The max amount of victims.
     * @return The list of targets.
     */
    public List<Entity> getMultihitTargets(Entity entity, Entity target, int max) {
        List<Entity> list = new ArrayList<>();
        list.add(target);
        boolean npc = target instanceof NPC;
        for (Entity e : npc ? RegionManager.getLocalNpcs(target, 1) : RegionManager.getLocalPlayers(target, 1)) {
            if (e != target && e != entity && CombatStyle.MAGIC.getSwingHandler().canSwing(entity, e) != InteractionType.NO_INTERACT) {
                list.add(e);
            }
            if (--max < 1) {
                break;
            }
        }
        return list;
    }

    /**
     * Visualizes the impact.
     *
     * @param entity The entity.
     * @param target The target.
     * @param state  The battle state.
     */
    public void visualizeImpact(Entity entity, Entity target, BattleState state) {
        boolean combatVictim = target == entity.getProperties().getCombatPulse().getVictim();
        if (state.getEstimatedHit() == -1) {
            if (combatVictim) {
                sendSound(target, new Audio(227, 1, 20));
            }
            target.graphics(SPLASH_GRAPHIC);
            return;
        }
        target.graphics(endGraphic);
        if (combatVictim) {
            sendSound(target, new Audio(impactSound, 1, 20));
        }
    }

    @Override
    public void visualize(Entity entity, Node target) {
        entity.graphics(graphic);
        if (projectile != null) {
            if (target instanceof Entity) {
                projectile.transform(entity, (Entity) target, entity instanceof NPC, 58, 10).send();
            } else {
                projectile.transform(entity, target.getLocation(), entity instanceof NPC, 58, 10).send();
            }
        }
        if (entity.getProperties().getAutocastSpell() == this && (entity instanceof Player || animation == null)) {
            entity.animate(AUTOCAST_ANIMATION);
        } else {
            entity.animate(animation);
        }
        sendSound(entity);
    }

    @Override
    public boolean cast(Entity entity, Node target) {
        if (!meetsRequirements(entity, true, false)) {
            return false;
        }
        if (target instanceof Entity) {
            entity.face((Entity) target);
        }
        entity.getProperties().setSpell(this);
        if (entity instanceof Player && target instanceof NPC && target.getId() == NPCDefinition.MAX_HIT_DUMMY) {
            Player player = (Player) entity;
            NPC npc = (NPC) target;
            if (player.sendDonorNotification("to use this dummy.")) {
                return false;
            }
            BattleState battleState = new BattleState();
            this.visualize(player, npc);
            int delay = player.getSwingHandler(false).swing(entity, npc, battleState);
            if (delay < 0) {
                return false;
            }
            World.submit(new Pulse(delay + 1) {

                @Override
                public boolean pulse() {
                    visualizeImpact(player, npc, battleState);
                    npc.getImpactHandler().manualHit(player,
                        CombatSwingHandler.getMaximumHit(player, npc),
                        ImpactHandler.HitsplatType.NORMAL);
                    npc.getSkills().setLifepoints(65000);
                    return true;
                }
            });
            return true;
        }
        if (entity.getProperties().getCombatPulse().isAttacking()) {
            entity.getProperties().getCombatPulse().setVictim(target);
            entity.getProperties().getCombatPulse().updateStyle();
            entity.getProperties().getCombatPulse().start();
            return true;
        }
        entity.getProperties().getCombatPulse().attack(target);
        return true;
    }

    /**
     * Gets the targets list.
     *
     * @param entity The entity
     * @param target The target.
     * @return The targets array.
     */
    public BattleState[] getTargets(Entity entity, Entity target) {
        return new BattleState[]{ new BattleState(entity, target) };
    }

    /**
     * Gets the accuracy modifier.
     *
     * @return The accuracy modifier.
     */
    public double getAccuracyMod() {
        return type.getAccuracyMod();
    }

    /**
     * @return the type.
     */
    public SpellType getType() {
        return type;
    }

    /**
     * @return the animation.
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * Gets the splash graphic.
     *
     * @return The splash graphic.
     */
    public Graphics getSplashGraphic() {
        return SPLASH_GRAPHIC;
    }

    /**
     * @param componentId the componentId to set.
     */
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    /**
     * @return the componentId.
     */
    public int getComponentId() {
        return componentId;
    }

}
