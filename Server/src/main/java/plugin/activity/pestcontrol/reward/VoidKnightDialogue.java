package plugin.activity.pestcontrol.reward;

import org.gielinor.game.content.dialogue.DialoguePlugin;
import org.gielinor.game.content.dialogue.OptionSelect;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;

/**
 * Represents the void knight dialogue.
 *
 * @author 'Vexia
 */
public final class VoidKnightDialogue extends DialoguePlugin {

    /**
     * Repreents the void knight ids.
     */
    public static final int[] IDS = new int[]{ 3786, 3788, 3789, 5956 };

    /**
     * Constructs a new {@code VoidKnightDialogue} {@code Object}.
     */
    public VoidKnightDialogue() {
        /**
         * empty.
         */
    }

    /**
     * Constructs a new {@code VoidKnightDialogue} {@code Object}.
     *
     * @param player the player.
     */
    public VoidKnightDialogue(Player player) {
        super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
        return new VoidKnightDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        npc("Hi, how can I help you?");
        stage = 0;
        return true;
    }

    @Override
    public boolean handle(int interfaceId, OptionSelect optionSelect) {
        switch (stage) {
            case 0:
                options("Who are you?", "What is this place?", "I'm fine thanks.");
                stage = 1;
                break;
            case 1:
                switch (optionSelect) {
                    case THREE_OPTION_ONE:
                        player("Who are you?");
                        stage = 10;
                        break;
                    case THREE_OPTION_TWO:
                        player("What is this place?");
                        stage = 20;
                        break;
                    case THREE_OPTION_THREE:
                        player("I'm fine thanks.");
                        stage = 30;
                        break;
                }
                break;
            case 10:
                npc("I'm a Void Knight, one of the order of Guthix. We are", "warriors of balance who do Guthix's work here in", "Gielinor.");
                stage = 11;
                break;
            case 11:
                options("Wow, can I join?", "What kind of work?", "What's 'Gielinor'?", "Uh huh, sure.");
                stage = 12;
                break;
            case 12:
                switch (optionSelect) {
                    case FOUR_OPTION_ONE:
                        player("Wow, can I join?");
                        stage = 180;
                        break;
                    case FOUR_OPTION_TWO:
                        player("What kind of work?");
                        stage = 17;
                        break;
                    case FOUR_OPTION_THREE:
                        player("What's 'Gielinor'?");
                        stage = 15;
                        break;
                    case FOUR_OPTION_FOUR:
                        player("Uh huh, sure.");
                        stage = 14;
                        break;
                }
                break;
            case 14:
                end();
                break;
            case 15:
                npc("It is the name that Guthix gave to this world, so we", "honour him with its use.");
                stage = 16;
                break;
            case 16:
                end();
                break;
            case 17:
                npc("Ah well you see we try to keep Gielinor as Guthix", "intended, it's very challenging. Actually we've been", "having some problems recently, maybe you could help", "us?");
                stage = 18;
                break;
            case 18:
                options("Yeah ok, what's the problem?", "What's 'Gielinor'?", "I'd rather not, sorry.");
                stage = 19;
                break;
            case 19:
                switch (optionSelect) {
                    case THREE_OPTION_ONE:
                        player("Yeah ok, what's the problem?");
                        stage = 191;
                        break;
                    case THREE_OPTION_TWO:
                        player("What's 'Gielinor'?");
                        stage = 15;
                        break;
                    case THREE_OPTION_THREE:
                        player("I'd rather not sorry.");
                        stage = 190;
                        break;
                }
                break;
            case 180:
                npc("Entry is strictly invite only, however we do need help", "continuing Guthix's work.");
                stage = 181;
                break;
            case 181:
                player("What kind of work?");
                stage = 17;
                break;
            case 190:
                end();
                break;
            case 191:
                npc("Well the order has become quite diminished over the", "years, it's a very long process to learn the skills of a", "Void Knight. Recently there have been breaches into", "our realm from somewhere else, and strange creatures");
                stage = 192;
                break;
            case 192:
                npc("have been pouring through. We can't let that happen,", "and we'd be very grateful if you'd help us.");
                stage = 193;
                break;
            case 193:
                options("How can I help?", "Sorry, but I can't.");
                stage = 194;
                break;
            case 194:
                switch (optionSelect) {
                    case TWO_OPTION_ONE:
                        player("How can I help?");
                        stage = 195;
                        break;
                    case TWO_OPTION_TWO:
                        player("Sorry, but I can't.");
                        stage = 190;
                        break;
                }
                break;
            case 195:
                npc("We send launchers from our outpost to the nearby", "islands. If you go and wait in the lander there that'd", "really help.");
                stage = 196;
                break;
            case 196:
                end();
                break;
            case 20:
                npc("This is our outpost. From here we send launchers out to", "the nearby islands to beat back the invaders.");
                stage = 21;
                break;
            case 21:
                options("What invaders?", "How can I help?", "Good luck with that.");
                stage = 22;
                break;
            case 22:
                switch (optionSelect) {
                    case THREE_OPTION_ONE:
                        player("What invaders?");
                        stage = 24;
                        break;
                    case THREE_OPTION_TWO:
                        player("How can I help?");
                        stage = 195;
                        break;
                    case THREE_OPTION_THREE:
                        player("Good luck with that.");
                        stage = 23;
                        break;
                }
                break;
            case 23:
                end();
                break;
            case 24:
                npc("Recently there have been breaches into our realm from", "somewhere else, and strange creatures have been", "pouring through. We can't let that happen, and we'd be", "very grateful if you'd help us.");
                stage = 193;
                break;
            case 30:
                end();
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return IDS;
    }
}
