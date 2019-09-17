package plugin.interaction.item.withplayer;

import org.gielinor.game.interaction.NodeUsageEvent;
import org.gielinor.game.interaction.UseWithHandler;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.item.Item;
import org.gielinor.rs2.plugin.Plugin;

/**
 * Handles using a field ration on a player.
 * @author Emperor
 *
 */
public final class FieldRationUsage extends UseWithHandler {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        addHandler(7934, PLAYER_TYPE, this);
        return this;
    }

    @Override
    public boolean handle(NodeUsageEvent event) {
        Item item = (Item) event.getUsed();
        Player target = (Player) event.getUsedWith();
        if (target == null || !target.isActive() || target.getLocks().isInteractionLocked()) {
            event.getPlayer().getActionSender().sendMessage("The other player is currently busy.");
            return true;
        }
        if (event.getPlayer().getInventory().remove(item)) {
            target.getSkills().heal(12);
            target.getActionSender().sendMessage("You have been healed by a field ration.");
            event.getPlayer().getActionSender().sendMessage("You use the field ration to heal the other player.");
            return true;
        }
        return false;
    }

}
