package plugin.interaction.item.withitem;

import org.gielinor.game.content.global.achievementold.AchievementDiary;
import org.gielinor.game.content.global.achievementold.impl.AchievementTask;
import org.gielinor.game.content.skill.free.magic.MagicSpell;
import org.gielinor.game.content.skill.free.magic.Runes;
import org.gielinor.game.node.Node;
import org.gielinor.game.node.entity.Entity;
import org.gielinor.game.node.entity.combat.equipment.SpellType;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.entity.player.link.SpellBookManager.SpellBook;
import org.gielinor.game.node.entity.player.link.audio.Audio;
import org.gielinor.game.node.item.Item;
import org.gielinor.game.world.update.flag.context.Animation;
import org.gielinor.game.world.update.flag.context.Graphics;
import org.gielinor.rs2.plugin.Plugin;

/**
 * Represents the enchant spells.
 *
 * @author Emperor
 * @author 'Vexia
 * @version 1.0
 */
public final class EnchantSpell extends MagicSpell {

    /**
     * The animation.
     */
    private static final Animation ANIMATION = Animation.create(712);

    /**
     * The graphic.
     */
    private static final Graphics GRAPHIC = new Graphics(114, 96);

    /**
     * The enchantable jewellery array.
     */
    private final Item[][] jewellery;

    /**
     * Constructs a new {@code EnchantSpell} {@code Object}.
     */
    public EnchantSpell() {
        this.jewellery = null;
    }

    /**
     * Constructs a new {@code EnchantSpell} {@code Object}.
     *
     * @param level     The level required.
     * @param jewellery The jewellery this spell is able to echant.
     * @param runes     The runes required.
     */
    public EnchantSpell(int level, double experience, Item[][] jewellery, Item[] runes) {
        super(SpellBook.MODERN, level, experience, ANIMATION, GRAPHIC, new Audio(115, 1, 0), runes);
        this.jewellery = jewellery;
    }

    @Override
    public boolean cast(Entity entity, Node target) {
        if (!(target instanceof Item) || !(entity instanceof Player)) {
            return false;
        }
        Player p = (Player) entity;
        p.getInterfaceState().setViewedTab(6);
        Item item = (Item) target;
        Item enchanted = null;
        for (Item[] arr : jewellery) {
            if (arr[0].getId() == item.getId()) {
                enchanted = new Item(arr[1].getId());
                break;
            }
        }
        if (enchanted == null) {
            p.getActionSender().sendMessage("You can't use this spell on this item.");
            return false;
        }
        if (!meetsRequirements(entity, true, true)) {
            return false;
        }
        visualize(entity, target);
        if (p.getInventory().remove(item)) {
            p.getInventory().add(enchanted);
            if (enchanted.getId() == 1731) {
                if ((p.getLocation().getRegionId() == 12850 || p.getLocation().getRegionId() == 12851) &&
                    p.getAchievementRepository().isCount(AchievementTask.CRAFT_AMULET_POWER, 2)) {
                    AchievementDiary.finalize(p, AchievementTask.CRAFT_AMULET_POWER);
                }
            }
        }
        return true;
    }

    @Override
    public Plugin<SpellType> newInstance(SpellType arg) throws Throwable {
        // TODO 317 Bracelets
        SpellBook.MODERN.register(1155, new EnchantSpell(7, 17.5, new Item[][]{ { new Item(1637), new Item(2550) }, { new Item(1656), new Item(3853) }, { new Item(1694), new Item(1727) } }, new Item[]{ new Item(Runes.COSMIC_RUNE.getId(), 1), new Item(Runes.WATER_RUNE.getId(), 1) }));
        SpellBook.MODERN.register(1165, new EnchantSpell(27, 37, new Item[][]{ { new Item(1639), new Item(2552) }, { new Item(1658), new Item(5521) }, { new Item(1696), new Item(1729) } }, new Item[]{ new Item(Runes.COSMIC_RUNE.getId(), 1), new Item(Runes.AIR_RUNE.getId(), 3) }));
        SpellBook.MODERN.register(1176, new EnchantSpell(49, 59, new Item[][]{ { new Item(1641), new Item(2568) }, { new Item(1660), new Item(11194) }, { new Item(1698), new Item(1725) } }, new Item[]{ new Item(Runes.COSMIC_RUNE.getId(), 1), new Item(Runes.FIRE_RUNE.getId(), 5) }));
        SpellBook.MODERN.register(1180, new EnchantSpell(57, 67, new Item[][]{ { new Item(1643), new Item(2570) }, { new Item(1662), new Item(11090) }, { new Item(1700), new Item(1731) } }, new Item[]{ new Item(Runes.COSMIC_RUNE.getId(), 1), new Item(Runes.EARTH_RUNE.getId(), 10) }));
        SpellBook.MODERN.register(1187, new EnchantSpell(68, 78, new Item[][]{ { new Item(1645), new Item(2572) }, { new Item(1664), new Item(11105) }, { new Item(1702), new Item(1712) }, { new Item(11115), new Item(11118) } }, new Item[]{ new Item(Runes.COSMIC_RUNE.getId(), 1), new Item(Runes.WATER_RUNE.getId(), 15), new Item(Runes.EARTH_RUNE.getId(), 15) }));
        SpellBook.MODERN.register(6003, new EnchantSpell(87, 97, new Item[][]{ { new Item(6575), new Item(6583) }, { new Item(6577), new Item(11128) }, { new Item(11130), new Item(11133) }, { new Item(26581), new Item(26585) } }, new Item[]{ new Item(Runes.COSMIC_RUNE.getId(), 1), new Item(Runes.FIRE_RUNE.getId(), 20), new Item(Runes.EARTH_RUNE.getId(), 20) }));
        return this;
    }
}