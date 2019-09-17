package plugin.interaction.object;

import org.gielinor.cache.def.impl.NPCDefinition;
import org.gielinor.cache.def.impl.ObjectDefinition;
import org.gielinor.game.component.Component;
import org.gielinor.game.content.global.action.DoorActionHandler;
import org.gielinor.game.interaction.OptionHandler;
import org.gielinor.game.node.Node;
import org.gielinor.game.node.entity.combat.CombatStyle;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.object.GameObject;
import org.gielinor.game.world.map.Location;
import org.gielinor.rs2.plugin.Plugin;

/**
 * Represents the quest node plugin handler.
 *
 * @author 'Vexia
 */
public class PriestInPerilOptionPlugin extends OptionHandler {

    /**
     * (non-Javadoc)
     *
     * @see org.gielinor.rs2.plugin.Plugin#newInstance(java.lang.Object)
     */
    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ObjectDefinition.forId(3444).getConfigurations().put("option:open", this);
        /** the gate in the chamber near the dog. */
        ObjectDefinition.forId(3445).getConfigurations().put("option:open", this);
        /** the gate in the temple to get to the other side. */
        ObjectDefinition.forId(30707).getConfigurations().put("option:open", this);
        /** the "knock-at" door. */
        ObjectDefinition.forId(30707).getConfigurations().put("option:knock-at", this);
        /** the "knock-at" door. */
        ObjectDefinition.forId(30708).getConfigurations().put("option:open", this);
        /** the "knock-at" door. */
        ObjectDefinition.forId(30708).getConfigurations().put("option:knock-at", this);
        /** the "knock-at" door. */
        ObjectDefinition.forId(30575).getConfigurations().put("option:climb-up", this);
        /** represents the ladder to climb back up. */
        ObjectDefinition.forId(30575).getConfigurations().put("option:climb-up", this);
        /** represents the ladder to climb back up. */
        ObjectDefinition.forId(30728).getConfigurations().put("option:open", this);
        /** the coffin. */
        ObjectDefinition.forId(3463).getConfigurations().put("option:open", this);
        /** the drezel door. */
        ObjectDefinition.forId(3463).getConfigurations().put("option:talk-through", this);
        /** talk-through. */
        ObjectDefinition.forId(3485).getConfigurations().put("option:search", this);
        /** teh well. */
        ObjectDefinition.forId(3496).getConfigurations().put("option:study", this);
        /** teh golden hammer */
        ObjectDefinition.forId(3496).getConfigurations().put("option:take-from", this);
        /** the golden hammer. */
        ObjectDefinition.forId(3498).getConfigurations().put("option:study", this);
        /** teh golden needle. */
        ObjectDefinition.forId(3498).getConfigurations().put("option:take-from", this);
        /** the golden needle. */
        ObjectDefinition.forId(3495).getConfigurations().put("option:study", this);
        /** teh golden pot. */
        ObjectDefinition.forId(3495).getConfigurations().put("option:take-from", this);
        /** the golden pot. */
        ObjectDefinition.forId(3497).getConfigurations().put("option:study", this);
        /** teh golden feather. */
        ObjectDefinition.forId(3497).getConfigurations().put("option:take-from", this);
        /** the golden feather. */
        ObjectDefinition.forId(3494).getConfigurations().put("option:study", this);
        /** teh golden candle. */
        ObjectDefinition.forId(3494).getConfigurations().put("option:take-from", this);
        /** the golden candle. */
        ObjectDefinition.forId(3499).getConfigurations().put("option:study", this);
        /** teh golden key/iron. */
        ObjectDefinition.forId(3499).getConfigurations().put("option:take-from", this);
        /** the golden key/iron */
        ObjectDefinition.forId(3493).getConfigurations().put("option:study", this);
        /** teh tinder box. */
        ObjectDefinition.forId(3493).getConfigurations().put("option:take-from", this);
        /** the golden tinder box. */
        ObjectDefinition.forId(3443).getConfigurations().put("option:pass-through", this);
        /** the barrier. */
        ObjectDefinition.forId(30573).getConfigurations().put("option:open", this);
        /** the door to get back. */
        NPCDefinition.forId(3487).getConfigurations().put("option:attack", this);
        /** represents the dog attack option. */
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.gielinor.game.interaction.OptionHandler#handle(org.gielinor.game.node.entity.player.Player,
     * org.gielinor.game.node.Node, java.lang.String)
     */
    @Override
    public boolean handle(Player player, Node node, String option) {
        int id = node instanceof GameObject ? ((GameObject) node).getId() : ((NPC) node).getId();
        switch (option) {
            case "study":
                player.getInterfaceState().open(new Component(272));
                int item = 0;
                String message = "";
                if (id == 3496) {
                    item = 2949;
                    message = "Saradomin is the hammer that crushes evil everywhere.";
                }
                if (id == 3498) {
                    item = 2951;
                    message = "Saradomin is the needle that binds our lives together.";
                }
                if (id == 3495) {
                    item = 2948;
                    message = "Saradomin is the vessle that keeps our lives from harm.";
                }
                if (id == 3497) {
                    item = 2950;
                    message = "Saradomin is the delicate touch that brushes us with love.";
                }
                if (id == 3494) {
                    item = 2947;
                    message = "Saradomin is the light that shines throughout our lives.";
                }
                if (id == 3499) {
                    if (!player.getGameAttributes().getAttribute("priest_in_peril:key", false)) {
                        item = 2945;
                    } else {
                        item = 2944;
                    }
                    message = "Saradomin is the key that unlocks the mysteries of life.";
                }
                if (id == 3493) {
                    item = 2946;
                    message = "Saradomin is the spark that lights the fire in our hearts.";
                }
                player.getActionSender().sendString498(message, 272, 17);
                player.getActionSender().sendItemZoomOnInterface(item, 175, 272, 4);
                break;
            case "take-from":
                player.getImpactHandler().handleImpact(player, 2, CombatStyle.MELEE);
                player.getActionSender().sendMessage("A holy power prevents you stealing from the monument!");
                break;
        }
        switch (id) {
            case 3444:
                DoorActionHandler.handleDoor(player, (GameObject) node);
                break;
            case 30573:
                player.getProperties().setTeleportLocation(Location.create(3440, 9887, 0));
                player.getActionSender().sendMessage("You cilmb down through the trap door...");
                break;
            case 3443:
                /** the barrier. */
                if (!player.getQuestRepository().isComplete("Priest in Peril")) {
                    player.getActionSender().sendMessage("A magic force prevents you from passing through.");
                } else {
                    player.getProperties().setTeleportLocation(Location.create(3425, 3485, 0));
                    player.getActionSender().sendMessage("You pass through the holy barrier.");
                }
                break;
            case 3485:
                /** the well. */
                player.getDialogueInterpreter().sendPlaneMessage("You look down the well and see the filthy polluted water of the river", "Salve moving slowly along.");
                break;
            case 3463:
                /** the drezel door. */
                switch (option) {
                    case "open":
                        DoorActionHandler.handleDoor(player, (GameObject) node);
                        break;
                    case "talk-through":
                        player.getDialogueInterpreter().open(1047, NPC.create(1047, player.getLocation()));
                        break;
                }
                break;
            case 30728:
                /** the coffin. */
                player.getDialogueInterpreter().sendDialogues(player, null, "It sounds like there's something alive inside it. I don't", "think it would be a very good idea to open it...");
                break;
            case 3445:/* the other gate. */
                DoorActionHandler.handleDoor(player, (GameObject) node);
                break;
            case 30707:/** the door to the church. */
            case 30708:
                switch (option) {
                    case "open":
                        DoorActionHandler.handleDoor(player, (GameObject) node);
                        break;
                }
                break;
            case 30575:
                /** represents the ladder to get back up. */
                player.getProperties().setTeleportLocation(Location.create(3405, 3506, 0));
                break;
            case 3487:
                player.getProperties().getCombatPulse().stop();
                break;
        }
        return true;
    }

}
