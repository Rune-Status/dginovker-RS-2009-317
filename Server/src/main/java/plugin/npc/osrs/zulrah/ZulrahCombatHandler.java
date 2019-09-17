package plugin.npc.osrs.zulrah;

import org.gielinor.game.node.entity.combat.CombatStyle;
import org.gielinor.game.node.entity.combat.equipment.SwitchAttack;
import org.gielinor.game.node.entity.combat.handlers.MultiSwingHandler;
import org.gielinor.game.node.entity.impl.Projectile;

/**
 * Handles the combat for the Zulrah Boss NPC.
 *
 * @author Vexia
 */
public class ZulrahCombatHandler extends MultiSwingHandler {

    /**
     * The zulrah switch attacks.
     */
    private static final SwitchAttack[] ATTACKS = new SwitchAttack[]{
        new SwitchAttack(CombatStyle.MAGIC.getSwingHandler(), ZulrahNPC.SHOOT_ANIMAION, createProjectile(21994)),
        new SwitchAttack(CombatStyle.RANGE.getSwingHandler(), ZulrahNPC.SHOOT_ANIMAION, createProjectile(21996))
    };

    /**
     * Constructs a new @{Code ZulrahCombatHandler} object.
     */
    public ZulrahCombatHandler() {
        super(ATTACKS);
    }

    /**
     * Creates a projectile for zulrah.
     *
     * @param graphicId The graphic id.
     * @return The projectile.
     */
    public static Projectile createProjectile(int graphicId) {
        return Projectile.create(null, null, graphicId, 48, 36, 34, 20);
    }

}