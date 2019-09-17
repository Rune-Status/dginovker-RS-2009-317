package plugin.dialogue;

import org.gielinor.game.content.dialogue.DialoguePlugin;
import org.gielinor.game.content.dialogue.FacialExpression;
import org.gielinor.game.content.dialogue.OptionSelect;
import org.gielinor.game.content.global.shop.Shops;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;

/**
 * Represents the dialogue plugin used for the al kharid shop keepers.
 *
 * @author 'Vexia
 * @version 1.0
 */
public final class AlKharidShopKeeperDialogue extends DialoguePlugin {

    /**
     * Constructs a new {@code AlKahridShopKeeperDialogue} {@code Object}.
     */
    public AlKharidShopKeeperDialogue() {
        /**
         * empty.
         */
    }

    /**
     * Constructs a new {@code AlKahridShopKeeperDialogue} {@code Object}.
     *
     * @param player the player.
     */
    public AlKharidShopKeeperDialogue(Player player) {
        super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
        return new AlKharidShopKeeperDialogue(player);
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
                interpreter.sendOptions("Select an Option", "Yes, please. What are you selling?", "How should I use your shop?", "No, thanks.");
                stage = 1;
                break;
            case 1:
                switch (optionSelect) {
                    case THREE_OPTION_ONE:
                        end();
                        Shops.AL_KHARID_GENERAL_STORE.open(player);
                        break;
                    case THREE_OPTION_TWO:
                        interpreter.sendDialogues(npc, FacialExpression.NO_EXPRESSION, "I'm glad you ask! You can buy as many of the items", "stocked as you wish. You can also sell most items to the", "shop.");
                        stage = 20;
                        break;
                    case THREE_OPTION_THREE:
                        end();
                        break;

                }
                break;
            case 20:
                end();
                break;
        }
        return true;
    }


    @Override
    public int[] getIds() {
        return new int[]{ 525, 524 };
    }

}
