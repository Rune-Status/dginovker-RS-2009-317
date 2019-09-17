package org.gielinor.rs2.pulse.impl;

import java.util.HashMap;
import java.util.Map;

import org.gielinor.game.interaction.DestinationFlag;
import org.gielinor.game.interaction.OptionHandler;
import org.gielinor.game.interaction.UseWithHandler;
import org.gielinor.game.node.Node;
import org.gielinor.game.node.entity.Entity;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.world.map.Direction;
import org.gielinor.game.world.map.Location;
import org.gielinor.game.world.map.Point;
import org.gielinor.game.world.map.path.Path;
import org.gielinor.game.world.map.path.Pathfinder;
import org.gielinor.game.world.map.zone.ZoneBorders;
import org.gielinor.net.packet.PacketRepository;
import org.gielinor.net.packet.context.PlayerContext;
import org.gielinor.net.packet.out.ClearMinimapFlag;
import org.gielinor.rs2.pulse.Pulse;

/**
 * Handles a movement task.
 *
 * @author Emperor
 */
public abstract class MovementPulse extends Pulse {

    public static final Map<ZoneBorders, Location> ZONE_BORDERS = new HashMap<>();

    static {
//        ZONE_BORDERS.put(GodwarsBossNPC.ZAMORAK_CHAMBER, null);
//        ZONE_BORDERS.put(GodwarsBossNPC.ARMADYL_CHAMBER, null);
//        ZONE_BORDERS.put(new ZoneBorders(2821, 5301, 2821, 5301), Location.create(2824, 5301, 2)); // Armadyl Altar
//        ZONE_BORDERS.put(GodwarsBossNPC.SARADOMIN_CHAMBER, null);
//        ZONE_BORDERS.put(GodwarsBossNPC.BANDOS_CHAMBER, null);
//        ZONE_BORDERS.put(new ZoneBorders(2869, 5372, 2869, 5372), Location.create(2869, 5369, 2)); // Bandos Altar
    }

    /**
     * The moving entity.
     */
    protected Entity mover;

    /**
     * The destination node.
     */
    protected Node destination;

    /**
     * The destination's last location.
     */
    private Location last;

    /**
     * The pathfinder.
     */
    public Pathfinder pathfinder;

    /**
     * If running should be forced.
     */
    private boolean forceRun;

    /**
     * The option handler.
     */
    private OptionHandler optionHandler;

    /**
     * The use with handler.
     */
    private UseWithHandler useHandler;

    /**
     * The destination flag.
     */
    private DestinationFlag destinationFlag;

    /**
     * The location to interact from.
     */
    private Location interactLocation;

    /**
     * If the path couldn't be fully found.
     */
    private boolean near;

    /**
     * Constructs a new {@code MovementPulse} {@code Object}.
     *
     * @param mover       The moving entity.
     * @param destination The destination node.
     */
    public MovementPulse(Entity mover, Node destination) {
        this(mover, destination, null, false);
    }

    /**
     * Constructs a new {@code MovementPulse} {@code Object}.
     *
     * @param mover       The moving entity.
     * @param destination The destination node.
     * @param forceRun    If the entity is forced to run.
     */
    public MovementPulse(Entity mover, Node destination, boolean forceRun) {
        this(mover, destination, null, forceRun);
    }

    /**
     * Constructs a new {@code MovementPulse} {@code Object}.
     *
     * @param mover       The moving entity.
     * @param destination The destination node.
     * @param pathfinder  The pathfinder to use.
     */
    public MovementPulse(Entity mover, Node destination, Pathfinder pathfinder) {
        this(mover, destination, pathfinder, false);
    }

    /**
     * Constructs a new {@code MovementPulse} {@code Object}.
     *
     * @param mover         The moving entity.
     * @param destination   The destination node.
     * @param optionHandler The option handler used.
     */
    public MovementPulse(Entity mover, Node destination, OptionHandler optionHandler) {
        this(mover, destination, null, false);
        this.optionHandler = optionHandler;
    }

    /**
     * Constructs a new {@code MovementPulse} {@code Object}.
     *
     * @param mover       The moving entity.
     * @param destination The destination node.
     * @param useHandler  The use with handler used.
     */
    public MovementPulse(Entity mover, Node destination, UseWithHandler useHandler) {
        this(mover, destination, null, false);
        this.useHandler = useHandler;
    }

    /**
     * Constructs a new {@code MovementPulse} {@code Object}.
     *
     * @param mover           The moving entity.
     * @param destination     The destination node.
     * @param destinationFlag The destination flag.
     */
    public MovementPulse(Entity mover, Node destination, DestinationFlag destinationFlag) {
        this(mover, destination, null, false);
        this.destinationFlag = destinationFlag;
    }

    /**
     * Constructs a new {@code MovementPulse} {@code Object}.
     *
     * @param mover       The moving entity.
     * @param destination The destination node.
     * @param pathfinder  The pathfinder to use.
     * @param forceRun    If the entity is forced to run.
     */
    public MovementPulse(Entity mover, Node destination, Pathfinder pathfinder, boolean forceRun) {
        super(1, mover, destination);
        this.mover = mover;
        this.destination = destination;
        if (pathfinder == null) {
            if (mover instanceof Player) {
                this.pathfinder = Pathfinder.SMART;
            } else {
                this.pathfinder = Pathfinder.DUMB;
            }
        } else {
            this.pathfinder = pathfinder;
        }
        this.forceRun = forceRun;
    }

    @Override
    public boolean update() {
        if (hasInactiveNode() || !mover.getViewport().getRegion().isActive()) {
            stop();
            return true;
        }
        if (!isRunning()) {
            return true;
        }
        findPath();
        if (mover.getLocation().equals(interactLocation)) {
            if (near || pulse()) {
                if (mover instanceof Player) {
                    if (near) {
                        ((Player) mover).getActionSender().sendMessage("I can't reach that.");
                    }
                    PacketRepository.send(ClearMinimapFlag.class, new PlayerContext((Player) mover));
                }
                stop();
                return true;
            }
        }
        return false;
    }

    @Override
    public void stop() {
        super.stop();
        if (destination instanceof Entity) {
            mover.face(null);
        }
        last = null;
    }

    /**
     * Finds a path to the destination, if necessary.
     */
    public void findPath() {
        if (mover instanceof NPC && mover.asNpc().isNeverWalks()) {
            return;
        }
        boolean inside = isInsideEntity(mover.getLocation());
        if (last != null && last.equals(destination.getLocation()) && !inside) {
            return;
        }
        Location loc = null;
        if (destinationFlag != null) {
            loc = destinationFlag.getDestination(mover, destination);
        }
        if (loc == null && optionHandler != null) {
            loc = optionHandler.getDestination(mover, destination);
        }
        if (loc == null && useHandler != null) {
            loc = useHandler.getDestination((Player) mover, destination);
        }
        if (loc == null && inside) {
            loc = findBorderLocation();
        }
        Path path = Pathfinder.find(mover, loc != null ? loc : destination, true, pathfinder);
        near = !path.isSuccessful() || path.isMoveNear();
        interactLocation = mover.getLocation();
        if (!path.getPoints().isEmpty()) {
            Point point = path.getPoints().getLast();
            interactLocation = Location.create(point.getX(), point.getY(), mover.getLocation().getZ());
            if (forceRun) {
                mover.getWalkingQueue().reset(forceRun);
            } else {
                mover.getWalkingQueue().reset();
            }
            while (!path.getPoints().isEmpty()) {
                point = path.getPoints().poll();
                mover.getWalkingQueue().addPath(point.getX(), point.getY());
            }
        }
        last = destination.getLocation();
        if (destination instanceof Entity) {
            mover.face((Entity) destination);
        } else {
            mover.face(null);
        }
    }

    /**
     * Finds the closest location next to the node.
     *
     * @return The location to walk to.
     */
    private Location findBorderLocation() {
        int size = destination.size();
        Location centerDest = destination.getLocation().transform(size >> 1, size >> 1, 0);
        Location center = mover.getLocation().transform(mover.size() >> 1, mover.size() >> 1, 0);
        Direction direction = Direction.getLogicalDirection(centerDest, center);
        Location delta = Location.getDelta(destination.getLocation(), mover.getLocation());
        main:
        for (int i = 0; i < 4; i++) {
            int amount = 0;
            switch (direction) {
                case NORTH:
                    amount = size - delta.getY();
                    break;
                case EAST:
                    amount = size - delta.getX();
                    break;
                case SOUTH:
                    amount = mover.size() + delta.getY();
                    break;
                case WEST:
                    amount = mover.size() + delta.getX();
                    break;
                default:
                    return null;
            }
            for (int j = 0; j < amount; j++) {
                for (int s = 0; s < mover.size(); s++) {
                    switch (direction) {
                        case NORTH:
                            if (!direction.canMove(mover.getLocation().transform(s, j + mover.size(), 0))) {
                                direction = Direction.get((direction.toInteger() + 1) & 3);
                                continue main;
                            }
                            break;
                        case EAST:
                            if (!direction.canMove(mover.getLocation().transform(j + mover.size(), s, 0))) {
                                direction = Direction.get((direction.toInteger() + 1) & 3);
                                continue main;
                            }
                            break;
                        case SOUTH:
                            if (!direction.canMove(mover.getLocation().transform(s, -(j + 1), 0))) {
                                direction = Direction.get((direction.toInteger() + 1) & 3);
                                continue main;
                            }
                            break;
                        case WEST:
                            if (!direction.canMove(mover.getLocation().transform(-(j + 1), s, 0))) {
                                direction = Direction.get((direction.toInteger() + 1) & 3);
                                continue main;
                            }
                            break;
                        default:
                            return null;
                    }
                }
            }
            Location location = mover.getLocation().transform(direction, amount);
            return location;
        }
        return null;
    }

    /**
     * Checks if the mover is standing on an invalid position.
     *
     * @param l The location.
     * @return <code>True</code> if so.
     */
    private boolean isInsideEntity(Location l) {
        if (!(destination instanceof Entity)) {
            return false;
        }
        if (((Entity) destination).getWalkingQueue().isMoving()) {
            return false;
        }
        Location loc = destination.getLocation();
        int size = destination.size();
        return Pathfinder.isStandingIn(l.getX(), l.getY(), mover.size(), mover.size(), loc.getX(), loc.getY(), size, size);
    }

    /**
     * Gets the forceRun.
     *
     * @return The forceRun.
     */
    public boolean isForceRun() {
        return forceRun;
    }

    /**
     * Sets the forceRun.
     *
     * @param forceRun The forceRun to set.
     */
    public void setForceRun(boolean forceRun) {
        this.forceRun = forceRun;
    }

    /**
     * Sets the current destination.
     *
     * @param destination The destination.
     */
    public void setDestination(Node destination) {
        this.destination = destination;
    }

    /**
     * Sets the last location.
     *
     * @param last The last location.
     */
    public void setLast(Location last) {
        this.last = last;
    }

}