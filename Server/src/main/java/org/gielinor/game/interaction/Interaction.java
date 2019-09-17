package org.gielinor.game.interaction;

import org.gielinor.game.node.Node;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.entity.player.ai.AIPlayer;
import org.gielinor.game.node.item.Item;
import org.gielinor.game.node.object.GameObject;
import org.gielinor.game.world.World;
import org.gielinor.game.world.update.flag.player.FaceLocationFlag;
import org.gielinor.net.packet.PacketRepository;
import org.gielinor.net.packet.context.InteractionOptionContext;
import org.gielinor.net.packet.context.PlayerContext;
import org.gielinor.net.packet.out.ClearMinimapFlag;
import org.gielinor.net.packet.out.InteractionOption;
import org.gielinor.rs2.model.container.Container;
import org.gielinor.rs2.pulse.Pulse;
import org.gielinor.rs2.pulse.impl.MovementPulse;

import plugin.activity.duelarena.DuelRule;
import plugin.activity.duelarena.DuelSession;

/**
 * Handles interaction between nodes.
 *
 * @author Emperor
 */
public final class Interaction {

    /**
     * The current options.
     */
    private Option[] options = new Option[8];

    /**
     * The player.
     */
    private final Node node;

    /**
     * Constructs a new {@code Interaction} {@code Object}.
     *
     * @param node The node reference.
     */
    public Interaction(Node node) {
        this.node = node;
    }

    /**
     * Sends the current option.
     *
     * @param node  The node.
     * @param index The index.
     * @param name  The option name.
     */
    public static void sendOption(Node node, int index, String name) {
        if (!(node instanceof Player)) {
            return;
        }
        PacketRepository.send(InteractionOption.class, new InteractionOptionContext((Player) node, index, name));
    }

    /**
     * Handles an interaction option being clicked.
     *
     * @param player The player using the option.
     * @param option The option used.
     */
    public void handle(final Player player, final Option option) {
        if (player.getLocks().isInteractionLocked() && !(player instanceof AIPlayer)) {
            return;
        }
        boolean hasHandler = option.getHandler() != null;
        String pulseType = "interaction:" + option.getName() + ":" + node.hashCode();
        boolean walk = hasHandler && option.getHandler().isWalk();
        if (!walk && hasHandler && option.getHandler().isWalk(player, node)) {
            walk = true;
        }
        if (walk && DuelRule.NO_MOVEMENT.enforce(player, false)) {
            if (option != Option._P_ATTACK) {
                DuelRule.NO_MOVEMENT.enforce(player, true);
                player.faceLocation(node.getLocation());
                PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
                return;
            }
            Player opponentPlayer = DuelSession.getExtension(player).getOpponent();
            if (node != opponentPlayer) {
                DuelRule.NO_MOVEMENT.enforce(player, true);
                player.faceLocation(node.getLocation());
                PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
                return;
            }
        }
        if (!hasHandler || walk) {
            handleWalkOption(player, option, pulseType);
        } else if (hasHandler) {
            player.debug("Option handler being used=" + option.getHandler().getClass().getSimpleName());
            handleDefaultOption(player, option, pulseType);
        } else {
            player.getPulseManager().runUnhandledAction(player, pulseType);
        }
    }

    /**
     * Handles an item option.
     *
     * @param player    The player.
     * @param option    The option.
     * @param container The container the item is in.
     */
    public void handleItemOption(final Player player, final Option option, final Container container) {
        if (player.getLocks().isInteractionLocked()) {
            return;
        }
        player.getPulseManager().clear("interaction:" + option.getName() + ":" + node.hashCode());
        World.submit(new Pulse(1, player) {

            @Override
            public boolean pulse() {
                if (player.getLocks().isInteractionLocked() || player.getZoneMonitor().interact(node, option)) {
                    return true;
                }
                if (option.getHandler() == null || !option.getHandler().handle(player, node, option.getName().toLowerCase())) {
                    player.getActionSender().sendMessage("Nothing interesting happens.");
                }
                return true;
            }
        });
    }

    /**
     * Handles an invalid interaction.
     *
     * @param player The player.
     * @param node   The node to interact with.
     */
    public static void handleInvalidInteraction(final Player player, final Node node, final Option option) {
        if (node == null) {
            return;
        }
        if (node.getLocation() != null) {
            if (player.getLocks().isMovementLocked()) {
                return;
            }
            player.getPulseManager().run(new MovementPulse(player, node) {

                @Override
                public boolean pulse() {
                    player.faceLocation(FaceLocationFlag.getFaceLocation(player, node));
                    if (player.getLocks().isInteractionLocked() || player.getZoneMonitor().interact(node, option)) {
                        return true;
                    }
                    player.getActionSender().sendMessage("Nothing interesting happens.");
                    return true;
                }
            }, "interaction:invalid:" + node.hashCode());
        } else {
            player.getPulseManager().runUnhandledAction(player, "interaction:invalid:" + node.hashCode());
        }
    }

    /**
     * Handles an option requiring the player to walk to the node.
     *
     * @param player    The player.
     * @param option    The option.
     * @param pulseType The pulse type.
     */
    private void handleWalkOption(final Player player, final Option option, String pulseType) {
        if (node.getLocation() == null) {
            player.getPulseManager().runUnhandledAction(player, pulseType);
            return;
        }
        if (player.getLocks().isMovementLocked()) {
            player.getPulseManager().clear(pulseType);
            return;
        }
        player.getPulseManager().run(new MovementPulse(player, node, option.getHandler()) {

            @Override
            public boolean pulse() {
                player.faceLocation(FaceLocationFlag.getFaceLocation(player, node));

                if (player.getLocks().isInteractionLocked() || player.getZoneMonitor().interact(node, option)) {
                    return true;
                }
                if (option.getHandler() == null || !option.getHandler().handle(player, node, option.getName().toLowerCase())) {
                    player.getActionSender().sendMessage("Nothing interesting happens.");
                }
                return true;
            }
        }, pulseType);
    }

    /**
     * Handles a default option.
     *
     * @param player    The player.
     * @param option    The option.
     * @param pulseType The pulse type.
     */
    private void handleDefaultOption(final Player player, final Option option, String pulseType) {
        if (!option.getHandler().isDelayed(player)) {
            if (player.getZoneMonitor().interact(node, option)) {
                return;
            }
            player.getProperties().getCombatPulse().stop();
            if (!option.getHandler().handle(player, node, option.getName().toLowerCase())) {
                player.getPulseManager().runUnhandledAction(player, pulseType);
            }
            return;
        }
        player.getPulseManager().run(new Pulse(1, player, node) {

            @Override
            public boolean pulse() {
                if (player.getLocks().isInteractionLocked() || player.getZoneMonitor().interact(node, option)) {
                    return true;
                }
                if (!option.getHandler().handle(player, node, option.getName().toLowerCase())) {
                    player.getActionSender().sendMessage("Nothing interesting happens.");
                }
                return true;
            }
        }, pulseType);
    }

    /**
     * Initializes the interaction options.
     *
     * @param nodeId The node id.
     * @param names  The option names.
     */
    public void init(int nodeId, String... names) {
        options = new Option[names.length];
        for (int i = 0; i < options.length; i++) {
            String option = names[i];
            if (option != null && !option.equals("null")) {
                final OptionHandler handler = Option.defaultHandler(node, nodeId, option);
                if (handler != null) {
                    set(new Option(option, i).setHandler(handler));
                }
            }
        }
    }

    /**
     * Sets the default options for a player.
     */
    public void setDefault() {
        if (node instanceof Player) {
            for (int i = 0; i < options.length; i++) {
                remove(i);
            }
            set(Option._P_FOLLOW);
            set(Option._P_TRADE);
            set(Option._P_ASSIST);
        } else if (node instanceof NPC) {
            NPC npc = (NPC) node;
            init(npc.getId(), npc.getDefinition().getOptions());
        } else if (node instanceof GameObject) {
            GameObject object = (GameObject) node;
            init(object.getId(), object.getDefinition().getOptions());
        } else if (node instanceof Item) {
            Item item = (Item) node;
            if (item.getLocation() != null) {
                init(item.getId(), item.getDefinition().getGroundOptions());
            } else {
                if (item.getId() != 0 && item.getDefinition() != null) {
                    init(item.getId(), item.getDefinition().getOptions());
                }
            }
        } else {
            throw new IllegalStateException("Unsupported node type - " + node);
        }
    }

    /**
     * Sets a new option.
     *
     * @param option The option.
     */
    public void set(Option option) {
        options[option.getIndex()] = option;
        sendOption(node, option.getIndex(), option.getName());
    }

    /**
     * Removes an option.
     *
     * @param option The option.
     * @return <code>True</code> if the option got removed, <code>False</code> if the option wasn't set.
     */
    public boolean remove(Option option) {
        if (options[option.getIndex()] == option) {
            remove(option.getIndex());
            return true;
        }
        return false;
    }

    /**
     * Removes an option.
     *
     * @param index The index.
     */
    public void remove(int index) {
        if (options[index] == null) {
            return;
        }
        options[index] = null;
        sendOption(node, index, "null");
    }

    /**
     * Gets the option on the requested slot.
     *
     * @param index The slot index.
     * @return The option on that slot, or {@code null} if there was no option.
     */
    public Option get(int index) {
        return options[index];
    }

    /**
     * Gets the options.
     *
     * @return The options.
     */
    public Option[] getOptions() {
        return options;
    }

    /**
     * Sets the options.
     *
     * @param options The options.
     */
    public void setOptions(Option[] options) {
        this.options = options;
    }
}
