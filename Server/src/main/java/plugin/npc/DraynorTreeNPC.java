package plugin.npc;

import java.util.List;

import org.gielinor.game.node.entity.combat.ImpactHandler.HitsplatType;
import org.gielinor.game.node.entity.impl.Animator.Priority;
import org.gielinor.game.node.entity.npc.AbstractNPC;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.world.World;
import org.gielinor.game.world.map.Location;
import org.gielinor.game.world.map.RegionManager;
import org.gielinor.game.world.update.flag.context.Animation;
import org.gielinor.utilities.misc.RandomUtil;

/**
 * Represents the abstract draynor tree npc.
 * @author 'Vexia
 * @version 1.0
 */
public final class DraynorTreeNPC extends AbstractNPC {

    /**
     * Represents the NPC ids of NPCs using this plugin.
     */
    private static final int[] ID = { 152 };

    /**
     * Represents the animation of the tree.
     */
    private static final Animation ANIMATION = new Animation(73, Priority.HIGH);

    /**
     * Represents the attack delay.
     */
    private int attackDelay;

    /**
     * Constructs a new {@code DraynorTreeNPC} {@code Object}.
     */
    public DraynorTreeNPC() {
        super(0, null, false);
    }

    /**
     * Constructs a new {@code DraynorTreeNPC} {@code Object}.
     * @param id the id.
     * @param location the location.
     */
    private DraynorTreeNPC(int id, Location location) {
        super(id, location, false);
    }

    @Override
    public AbstractNPC construct(int id, Location location, Object... objects) {
        return new DraynorTreeNPC(id, location);
    }

    @Override
    public void tick() {
        final List<Player> players = RegionManager.getLocalPlayers(this, 1);
        if (players.size() != 0) {
            if (attackDelay < World.getTicks()) {
                for (Player p : players) {
                    faceTemporary(p, 2);
                    getAnimator().forceAnimation(ANIMATION);
                    int hit = RandomUtil.random(2);
                    p.getImpactHandler().manualHit(this, hit, hit > 0 ? HitsplatType.NORMAL : HitsplatType.MISS);
                    attackDelay = World.getTicks() + 3;
                    p.animate(p.getProperties().getDefenceAnimation());
                    return;
                }
            }
        }
        super.tick();
    }

    @Override
    public int[] getIds() {
        return ID;
    }

}