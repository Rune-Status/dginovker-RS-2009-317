package plugin.dialogue;

import org.gielinor.game.component.Component;
import org.gielinor.game.content.dialogue.DialoguePlugin;
import org.gielinor.game.content.dialogue.FacialExpression;
import org.gielinor.game.content.dialogue.OptionSelect;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.item.Item;

/**
 * Handles the MakeOverMageDialogue dialogue.
 *
 * @author 'Vexia
 */
public class MakeOverMageDialogue extends DialoguePlugin {

    public MakeOverMageDialogue() {

    }

    public MakeOverMageDialogue(Player player) {
        super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {

        return new MakeOverMageDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        if (args.length == 2) {

            interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Hmm... you didn't feel any unexpected growths", "aywhere around your head just then did you?");
            stage = 600;
            return true;
        }
        interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Hello there! I am known as the make-over mage! I have", "spent many years researching magics that can change", "your physical appearance!");
        stage = 0;
        return true;
    }

    @Override
    public boolean handle(int interfaceId, OptionSelect optionSelect) {
        switch (stage) {
            case 0:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "I can alter your physical form for a small fee of only", "3000 gold coins! Would you like me to perform my", "magics upon you?");
                stage = 1;
                break;
            case 1:
                if (!player.getInventory().contains(7803, 1) && !player.getBank().contains(7803, 1) && !player.getEquipment().contains(7803, 1)) {
                    interpreter.sendOptions("Select an Option", "Tell me more about this 'make-over'.", "Sure. Do it.", "No thanks.", "Cool amulet! Can I have one?");
                } else {
                    interpreter.sendOptions("Select an Option", "Tell me more about this 'make-over'.", "Sure. Do it.", "No thanks.");
                }
                stage = 2;
                break;
            case 2:
                switch (optionSelect) {
                    case FOUR_OPTION_ONE:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Tell me more about this 'make-over'.");
                        stage = 10;
                        break;
                    case FOUR_OPTION_TWO:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Sure. Do it.");
                        stage = 20;
                        break;
                    case FOUR_OPTION_THREE:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "No thanks. I'm happy as Saradomin made me.");
                        stage = 19;
                        break;
                    case FOUR_OPTION_FOUR:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Cool amulet! Can I have one?");
                        stage = 40;
                        break;
                }
                break;
            case 20:
                if (player.getInventory().contains(Item.COINS, 3000)) {
                    interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "You of course agree that if by some accident you are", "turned into a frog you have no rights for compensation", "or refund.");
                    stage = 25;
                } else {
                    interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "I don't have 3000 gold coins on me...");
                    stage = 21;
                }
                break;
            case 21:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Well, go get it then. No freebies here!");
                stage = 22;
                break;
            case 22:
                end();
                break;
            case 25:
                end();
                player.getInterfaceState().open(new Component(5454));
                player.getConfigManager().set(261, player.getAppearance().isMale() ? 1 : 2);
                player.getConfigManager().set(262, player.getAppearance().getSkin().getColour() + 1);
                break;
            case 10:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Why, of course! Basically, and I will try and explain", "this so that you will understant it correctly,");
                stage = 11;
                break;
            case 11:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "I use my secret magical technique to melt your body", "down into a puddle of its elememnts.");
                stage = 12;
                break;
            case 12:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "When I have broken down all trace of your body, I", "then rebuild it into the form I am thinking of.");
                stage = 13;
                break;
            case 13:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Or, you know, somewhere vaguely close enough", "anyway.");
                stage = 14;
                break;
            case 14:
                interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Uh... that doesn't sound particularly safe to me...");
                stage = 15;
                break;
            case 15:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "It's as safe as houses! Why, I have only had thrity-six", "major accidents this month!");
                stage = 16;
                break;
            case 16:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "So what do you say? Feel like a change?");
                stage = 17;
                break;
            case 17:
                interpreter.sendOptions("Select an Option", "Sure. Do it.", "No thanks.");
                stage = 18;
                break;
            case 18:
                switch (optionSelect) {
                    case TWO_OPTION_ONE:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Sure. Do it.");
                        stage = 20;
                        break;
                    case TWO_OPTION_TWO:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "No thanks. I'm happy as Saradomin made me.");
                        stage = 19;
                        break;
                }
                break;
            case 19:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Ehhh... suit yourself.");
                stage = 900;
                break;
            case 900:
                end();
                break;
            case 40:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "No problem, but please remember that the amulet I will", "sell you is only a copy of my own. It contains no", "magical powers; and as such will only cost you 100", "coins.");
                stage = 41;
                break;
            case 41:
                interpreter.sendOptions("Select an Option", "Sure, here you go.", "No way! that's too expensive.");
                stage = 42;
                break;
            case 42:
                switch (optionSelect) {
                    case TWO_OPTION_ONE:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Sure, here you go.");
                        stage = 100;
                        break;
                    case TWO_OPTION_TWO:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "No way! That's too expensive.");
                        stage = 400;
                        break;

                }
                break;
            case 400:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "That's fair enough, my jewellery is not to everyone's", "taste.");
                stage = 401;
                break;
            case 401:
                end();
                break;
            case 600:
                interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Uh... No...?");
                stage = 601;
                break;
            case 601:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Good, good, I was worried for a second there!");
                stage = 602;
                break;
            case 602:
                interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Uh... Thanks, I guess.");
                stage = 603;
                break;
            case 603:
                end();
                break;
            case 100:
                if (player.getInventory().freeSlots() == 0) {
                    end();
                    player.getActionSender().sendMessage("You don't have enough inventory space.");
                }
                if (!player.getInventory().contains(Item.COINS, 100)) {
                    end();
                    player.getActionSender().sendMessage("You need 100 coins.");
                } else {
                    // you recieve an amulet in exchange for 100 coins.
                    // 7803
                    Item remove = new Item(Item.COINS, 100);
                    if (!player.getInventory().containsItem(remove)) {
                        end();
                        return true;
                    }
                    if (player.getInventory().remove(remove)) {
                        interpreter.sendItemMessage(7803, "You receive an amulet in exchange for 100 coins.");
                        player.getInventory().add(new Item(7803, 1));
                        stage = 101;
                    }
                }
                break;
            case 101:
                end();
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{ 2676 };
    }

}
