package plugin.dialogue;

import org.gielinor.game.content.dialogue.DialoguePlugin;
import org.gielinor.game.content.dialogue.FacialExpression;
import org.gielinor.game.content.dialogue.OptionSelect;
import org.gielinor.game.content.global.shop.Shops;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;

/**
 * Represents the dialogue plugin used for fortunato.
 *
 * @author 'Vexia
 * @version 1.0
 */
public final class FortunatoDialogue extends DialoguePlugin {

    /**
     * Constructs a new {@code FortunatoDialogue} {@code Object}.
     */
    public FortunatoDialogue() {
        /**
         * empty.
         */
    }

    /**
     * Constructs a new {@code FortunatoDialogue} {@code Object}.
     *
     * @param player the player.
     */
    public FortunatoDialogue(Player player) {
        super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
        return new FortunatoDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Can I help you at all?");
        stage = 0;
        return true;
    }

    @Override
    public boolean handle(int interfaceId, OptionSelect optionSelect) {

        switch (stage) {
            case 0:
                interpreter.sendOptions("Select an Option", "Yes, what are you selling?", "Not at the momennt.");
                stage = 1;
                break;
            case 1:

                switch (optionSelect) {
                    case TWO_OPTION_ONE:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Yes, what are you selling?");
                        stage = 10;
                        break;
                    case TWO_OPTION_TWO:
                        interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Then move along, you filthy ragamuffin, I have customers", "to server!");
                        stage = 24;
                        break;
                }

                break;
            case 10:
                end();
                Shops.FORTUNATOS_FINE_WINE.open(player);
                break;
            case 20:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Yes, indeed. The finest wine in Misthalin.");
                stage = 21;
                break;
            case 21:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Care to take a look at my wares?");
                stage = 269;
                break;
            case 269:
                interpreter.sendOptions("Select an Option", "Yes.", "Not at the moment.");
                stage = 22;
                break;
            case 22:

                switch (optionSelect) {
                    case TWO_OPTION_ONE:
                        end();
                        stage = 10;
                        break;
                    case TWO_OPTION_TWO:
                        interpreter.sendDialogues(player, FacialExpression.NO_EXPRESSION, "Not at the moment.");
                        stage = 23;
                        break;
                }

                break;
            case 23:
                interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "Then move along, you filthy ragamuffin, I have customers", "to server!");
                stage = 24;
                break;
            case 24:
                end();
                break;
        }

        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{ 3671 };
    }
}
