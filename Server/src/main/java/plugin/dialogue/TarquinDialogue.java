package plugin.dialogue;

import org.gielinor.game.content.dialogue.DialoguePlugin;
import org.gielinor.game.content.dialogue.FacialExpression;
import org.gielinor.game.content.dialogue.OptionSelect;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;

/**
 * Handles the TarquinDialogue dialogue.
 *
 * @author 'Vexia
 */
public class TarquinDialogue extends DialoguePlugin {

    public TarquinDialogue() {

    }

    public TarquinDialogue(Player player) {
        super(player);
    }

    @Override
    public int[] getIds() {
        return new int[]{ 3328 };
    }

    @Override
    public boolean handle(int interfaceId, OptionSelect optionSelect) {
        switch (stage) {
            case 0:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Hello old bean. Is there something I can help you", "with?");
                stage = 1;
                break;
            case 1:
                interpreter.sendOptions("Select an Option", "Who are you?", "Can you teach me about Canoeing?");
                stage = 2;
                break;
            case 2:
                switch (optionSelect) {
                    case TWO_OPTION_ONE:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Who are you?");
                        stage = 10;
                        break;
                    case TWO_OPTION_TWO:
                        interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "It's really quite simple. Just walk down to that tree on", "the water bank and chop it down.");
                        stage = 24;
                        break;

                }
                break;
            case 10:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "My name is Tarquin Marjoribanks.");
                stage = 11;
                break;
            case 11:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "I'd be suprised if you haven't already heard of me?");
                stage = 12;
                break;
            case 12:
                interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Why would I have heard of you Mr. Marjoribanks?");
                stage = 13;
                break;
            case 13:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "It's pronounced 'Marchbanks'!");
                stage = 14;
                break;
            case 14:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "You should know of me because I am a member of the", "royal family of Misthalin!");
                stage = 15;
                break;
            case 15:
                interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Are you related to King Roald?");
                stage = 16;
                break;
            case 16:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Oh yes! Quite closely actually.");
                stage = 17;
                break;
            case 17:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "I'm his 4th cousin, once removed on his mothers side.");
                stage = 18;
                break;
            case 18:
                interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Er... Okay. What are you doing here then?");
                stage = 19;
                break;
            case 19:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "I'm canoeing on the river! It's enormous fun! Would", "you like to know how?");
                stage = 20;
                break;
            case 20:
                interpreter.sendOptions("Select an Option", "Yes", "No");
                stage = 21;
                break;
            case 21:
                switch (optionSelect) {
                    case TWO_OPTION_ONE:
                        interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "It's really quite simple. Just walk down to that tree on", "the water bank and chop it down.");
                        stage = 24;
                        break;
                    case TWO_OPTION_TWO:
                        end();
                        break;

                }
                break;
            case 24:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Then take your hatchet to it and shape it how you like!");
                stage = 26;
                break;
            case 26:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "You look like you know your way around a tree, you can", "you can make many canoes.");
                stage = 27;
                break;
            case 27:
                end();
                break;
            case 25:
                end();
                break;
            case 100:
                break;
        }
        return true;
    }

    @Override
    public DialoguePlugin newInstance(Player player) {

        return new TarquinDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Hello there.");
        stage = 0;
        return true;
    }
}
