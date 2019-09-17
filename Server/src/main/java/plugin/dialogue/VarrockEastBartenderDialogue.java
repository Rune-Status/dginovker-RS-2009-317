package plugin.dialogue;

import org.gielinor.game.content.dialogue.DialoguePlugin;
import org.gielinor.game.content.dialogue.FacialExpression;
import org.gielinor.game.content.dialogue.OptionSelect;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.item.Item;
import org.gielinor.rs2.config.Constants;

/**
 * Handles the VarrockEastBartenderDialogue dialogue.
 *
 * @author 'Vexia
 */
public class VarrockEastBartenderDialogue extends DialoguePlugin {

    public VarrockEastBartenderDialogue() {

    }

    public VarrockEastBartenderDialogue(Player player) {
        super(player);
    }

    @Override
    public int[] getIds() {
        return new int[]{ 733 };
    }

    @Override
    public boolean handle(int interfaceId, OptionSelect optionSelect) {

        switch (stage) {
            case 0:
                interpreter.sendOptions("Select an Option", "A glass of your finest ale please.", "Can you recommend where an adventurer might make his fortune?", "Do you know where I can get some good equipment?");
                stage = 1;
                break;
            case 1:

                switch (optionSelect.getButtonId()) {
                    case 1:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "A glass of your finest ale please.");
                        stage = 10;
                        break;

                    case 2:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Can you recommend where an adventurer might make", "his fortune?");
                        stage = 20;
                        break;
                    case 3:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Do you know where I can get some good equipment?");
                        stage = 30;
                        break;
                }

                break;

            case 10:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "No problemo. That'll be 2 coins.");
                stage = 11;
                break;
            case 11:
                if (player.getInventory().contains(Item.COINS, 2)) {
                    player.getInventory().remove(new Item(Item.COINS, 2));
                    player.getInventory().add(new Item(1917, 1));
                    end();
                    player.getActionSender().sendMessage("You buy a pint of beer.");
                } else {
                    end();
                    player.getActionSender().sendMessage("You need 2 coins to buy ale.");
                }
                break;
            case 20:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Ooh I don't know if I should be giving away information,", "makes the computer game too easy.");
                stage = 21;
                break;
            case 21:
                interpreter.sendOptions("Select an Option", "Oh ah well...", "Computer game? What are you talking about?", "Just a small clue?");
                stage = 22;
                break;
            case 22:

                switch (optionSelect.getButtonId()) {
                    case 1:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Oh ah well...");
                        stage = 150;
                        break;
                    case 2:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Computer game? What are you talking about?");
                        stage = 160;
                        break;
                    case 3:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Just a small clue?");
                        stage = 170;
                        break;
                }

                break;
            case 160:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "This world around us... is a computer game.... called", Constants.SERVER_NAME + ".");
                stage = 161;
                break;
            case 161:
                interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Nope, still don't understand what you are talking about.", "What's a computer?");
                stage = 162;
                break;
            case 162:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "It's a sort of magic box thing, wich can do all sorts of", "stuff.");
                stage = 163;
                break;
            case 163:
                interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "I give up. You're obviously completely mad");
                stage = 164;
                break;
            case 164:
                end();
                break;
            case 150:
                end();
                break;
            case 30:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Well, there's the sword shop across the road, or there's", "also all sorts of shops up around the market.");
                stage = 31;
                break;
            case 31:
                end();
                break;
            case 170:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Go and talk to the bartender at the Holly Boar Inn, he", "doesn't seem to mind giving away clues.");
                stage = 171;
                break;
            case 171:
                end();
                break;
        }

        return true;
    }

    @Override
    public DialoguePlugin newInstance(Player player) {

        return new VarrockEastBartenderDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "What can I do yer for?");
        stage = 0;
        return true;
    }
}
