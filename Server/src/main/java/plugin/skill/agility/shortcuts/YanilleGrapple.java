package plugin.skill.agility.shortcuts;

import org.gielinor.cache.def.impl.ObjectDefinition;
import org.gielinor.game.content.skill.Skills;
import org.gielinor.game.interaction.OptionHandler;
import org.gielinor.game.node.Node;
import org.gielinor.game.node.entity.impl.ForceMovement;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.item.Item;
import org.gielinor.game.node.object.GameObject;
import org.gielinor.game.world.World;
import org.gielinor.game.world.map.Location;
import org.gielinor.game.world.update.flag.context.Animation;
import org.gielinor.net.packet.PacketRepository;
import org.gielinor.net.packet.context.MinimapStateContext;
import org.gielinor.net.packet.out.MinimapState;
import org.gielinor.rs2.plugin.Plugin;
import org.gielinor.rs2.pulse.Pulse;

/**
 * The plugin for the Yanille grapple shortcut.
 *
 * @author <a href="https://Gielinor.org">Gielinor Logan G.</a>
 */
public final class YanilleGrapple extends OptionHandler {

    /**
     * Represents the requirements array.
     */
    private static final int[][] REQUIREMENTS = new int[][]{ { Skills.AGILITY, 39 }, { Skills.RANGE, 21 }, { Skills.STRENGTH, 38 } };

    /**
     * Represents the mithril c'bow item.
     */
    private static final Item MITH_CBOW = new Item(9181);

    /**
     * Represents the mithril grapple item.
     */
    private static final Item MITH_GRAPPLE = new Item(9419);

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ObjectDefinition.forId(17047).getConfigurations().put("option:grapple", this);
        ObjectDefinition.forId(17048).getConfigurations().put("option:jump", this);
        return this;
    }

    @Override
    public boolean handle(final Player player, final Node node, String option) {
        switch (option) {
            case "jump":
                ForceMovement.run(player, player.getLocation(), ((GameObject) node).getLocation().getY() == 3075 ? Location.create(2556, 3075, 0) : Location.create(2556, 3072, 0), new Animation(7268), 10);
                break;
            case "grapple":
                for (int[] REQUIREMENT : REQUIREMENTS) {
                    if (player.getSkills().getLevel(REQUIREMENT[0]) < REQUIREMENT[1]) {
                        player.getDialogueInterpreter().sendPlaneMessage("You need at least 21 Ranged, 39 Agility and 38 Strength to do that.");
                        return true;
                    }
                }
                if (!player.getEquipment().containsItem(MITH_CBOW) || !player.getEquipment().containsItem(MITH_GRAPPLE)) {
                    player.getDialogueInterpreter().sendPlaneMessage("You need a Mithril crossbow and a Mithril grapple in order to do this.");
                    return true;
                }
                player.lock();
                World.submit(new Pulse(1, player) {

                    int counter = 1;

                    @Override
                    public boolean pulse() {
                        switch (counter++) {
                            case 2:
                                player.faceLocation(((GameObject) node).getLocation().getY() == 3075 ? Location.create(2556, 3072, 0) : Location.create(2556, 3075, 0));
                                break;
                            case 3:
                                player.getInterfaceState().openComponent(8677);
                                break;
                            case 4:
                                PacketRepository.send(MinimapState.class, new MinimapStateContext(player, 2));
                                player.getInterfaceState().removeTabs(0, 1, 2, 3, 4, 5, 6, 11, 12);
                                break;
                            case 7:
                                player.getProperties().setTeleportLocation(((GameObject) node).getLocation().getY() == 3075 ? Location.create(2556, 3074, 1) : Location.create(2556, 3073, 1));
                                break;
                            case 8:
                                player.getInterfaceState().close();
                                player.getInterfaceState().openDefaultTabs();
                                PacketRepository.send(MinimapState.class, new MinimapStateContext(player, 0));
                                player.unlock();
                                return true;
                        }
                        return false;
                    }
                });
                break;
        }
        return true;
    }

    @Override
    public Location getDestination(final Node node, final Node n) {
        return ((GameObject) n).getId() == 17050 ? Location.create(3032, 3388, 0) : null;
    }

}
