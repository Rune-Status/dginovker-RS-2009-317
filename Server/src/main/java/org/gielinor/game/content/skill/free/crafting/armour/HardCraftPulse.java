package org.gielinor.game.content.skill.free.crafting.armour;

import org.gielinor.game.content.skill.SkillPulse;
import org.gielinor.game.content.skill.Skills;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.entity.player.info.Perk;
import org.gielinor.game.node.item.Item;
import org.gielinor.game.world.update.flag.context.Animation;

/**
 * Represents the pulse used to craft hard leather.
 * @author 'Vexia
 *
 */
public final class HardCraftPulse extends SkillPulse<Item> {

    /**
     * Represents the animation to use.
     */
    private static final Animation ANIMATION = Animation.create(1249);

    /**
     * Represents the amount to make.
     */
    private int amount;

    /**
     * Represents the ticks passed.
     */
    private int ticks;

    /**
     * Constructs a new {@code HardCraftPulse} {@code Object}.
     * @param player the player.
     * @param node the node.
     * @param soft the soft.
     * @param amount the amount.
     */
    public HardCraftPulse(Player player, Item node, int amount) {
        super(player, node);
        this.amount = amount;
    }

    @Override
    public boolean checkRequirements() {
        if (player.getSkills().getLevel(Skills.CRAFTING) < 28) {
            player.getDialogueInterpreter().sendPlaneMessage("You need a crafting level of " + 28 + " to make a hardleather body.");
            return false;
        }
        if (!player.getInventory().contains(LeatherCrafting.NEEDLE, 1)) {
            return false;
        }
        if (!player.getInventory().contains(LeatherCrafting.HARD_LEATHER, 1)) {
            return false;
        }
        if (!player.getInventory().containsItem(LeatherCrafting.THREAD) && !Perk.NICE_THREADS.enabled(player)) {
            player.getDialogueInterpreter().sendPlaneMessage("You need thread to make this.");
            return false;
        }
        player.getInterfaceState().close();
        return true;
    }

    @Override
    public void animate() {
        if (ticks % 5 == 0) {
            player.animate(ANIMATION);
        }
    }

    @Override
    public boolean reward() {
        if (++ticks % 5 != 0) {
            return false;
        }
        if (player.getInventory().remove(new Item(LeatherCrafting.HARD_LEATHER))) {
            player.getInventory().add(new Item(1131));
            player.getSkills().addExperience(Skills.CRAFTING, 35);
            if (!Perk.NICE_THREADS.enabled(player)) {
                LeatherCrafting.decayThread(player);
                if (LeatherCrafting.isLastThread(player)) {
                    LeatherCrafting.removeThread(player);
                }
            }
        }
        amount--;
        return amount < 1;
    }

}
