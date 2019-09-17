package plugin.npc.familiar;

import org.gielinor.game.content.skill.member.summoning.familiar.Familiar;
import org.gielinor.game.content.skill.member.summoning.familiar.FamiliarSpecial;
import org.gielinor.game.content.skill.member.summoning.familiar.Forager;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.item.Item;
import org.gielinor.game.world.update.flag.context.Animation;
import org.gielinor.game.world.update.flag.context.Graphics;

/**
 * Represents the Albino Rat familiar.
 *
 * @author Aero
 * @author Vexia
 */
public class AlbinoRatNPC extends Forager {

    /**
     * The cheese item.
     */
    private static final Item CHEESE = new Item(1985, 4);

    /**
     * Constructs a new {@code AlbinoRatNPC} {@code Object}.
     */
    public AlbinoRatNPC() {
        this(null, 6847);
    }

    /**
     * Constructs a new {@code AlbinoRatNPC} {@code Object}.
     *
     * @param owner The owner.
     * @param id    The id.
     */
    public AlbinoRatNPC(Player owner, int id) {
        super(owner, id, 2200, 12067, 6, CHEESE);
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new AlbinoRatNPC(owner, id);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        if (produceItem(CHEESE)) {
            owner.lock(7);
            visualize(Animation.create(4934), Graphics.create(1384));
            return true;
        }
        return false;
    }

    @Override
    public int[] getIds() {
        return new int[]{ 6847, 6848 };
    }

}
