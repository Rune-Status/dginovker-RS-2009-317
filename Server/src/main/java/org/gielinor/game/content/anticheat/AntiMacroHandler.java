package org.gielinor.game.content.anticheat;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.gielinor.cache.misc.buffer.ByteBufferUtils;
import org.gielinor.game.content.skill.Skills;
import org.gielinor.game.node.entity.npc.NPC;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.node.entity.player.info.Perk;
import org.gielinor.game.world.World;
import org.gielinor.game.world.map.zone.ZoneRestriction;
import org.gielinor.parser.player.SavingModule;
import org.gielinor.utilities.misc.RandomUtil;

/**
 * Handles anti macroing.
 *
 * @author Emperor
 */
public final class AntiMacroHandler implements SavingModule {

    /**
     * The update frequency.
     */
    private static final int UPDATE_FREQUENCY = 50;
    /**
     * The ratio of firing events, the higher the less frequent.
     */
    public static final int FIRE_RATIO = 150;

    /**
     * The list of anti-macro events.
     */
    public static final Map<String, AntiMacroEvent> EVENTS = new HashMap<>();
    /**
     * The player.
     */
    private final Player player;
    /**
     * The last pulse tick.
     */
    private int nextPulse;
    /**
     * The current event.
     */
    private AntiMacroEvent event;
    /**
     * The experience monitors.
     */
    private ExperienceMonitor[] monitors = new ExperienceMonitor[Skills.SKILL_NAME.length];
    /**
     * The chance ratio of firing random events.
     */
    private final int[] chanceRatio = new int[Skills.SKILL_NAME.length];

    /**
     * Constructs a new {@code AntiMacroHandler} {@code Object}.
     *
     * @param player The player.
     */
    public AntiMacroHandler(Player player) {
        this.player = player;
    }

    /**
     * Checks if saving is required.
     *
     * @return <code>True</code> if so.
     */
    public boolean isSaveRequired() {
        return hasEvent() && event.isSaveRequired();
    }

    @Override
    public void save(ByteBuffer buffer) {
        if (hasEvent()) {
            buffer.put((byte) 1);
            ByteBufferUtils.putString(event.getName(), buffer);
            buffer.put((byte) 0);
            int index = buffer.position();
            event.save(buffer);
            buffer.put(index - 1, (byte) (buffer.position() - index));
        }
        buffer.put((byte) 0);
    }

    @Override
    public void parse(ByteBuffer buffer) {
        event = null;
        while (true) {
            switch (buffer.get() & 0xFF) {
                case 0:
                    return;
                case 1:
                    event = EVENTS.get(ByteBufferUtils.getString(buffer));
                    int length = buffer.get() & 0xFF;
                    ByteBuffer buf = ByteBuffer.allocate(length);
                    for (int i = 0; i < length; i++) {
                        buf.put(buffer.get());
                    }
                    buf.flip();
                    if (event != null) {
                        (event = event.create(player)).parse(buf);
                    }
                    break;
            }
        }
    }

    /**
     * Gets called every game pulse.
     */
    public void pulse() {
        if (World.getTicks() < nextPulse) {
            return;
        }
        if (Perk.EVENTS_BE_GONE.enabled(player)) {
            return;
        }
        if (!player.getLocks().isInteractionLocked() && !player.getLocks().isTeleportLocked() && !player.getLocks().isMovementLocked()) {
            for (int i = 0; i < monitors.length; i++) {
                ExperienceMonitor monitor = monitors[i];
                if (chanceRatio[i] > FIRE_RATIO) {
                    fireEvent(i);
                }
                if (monitor.getExperienceAmount() > 0) {
                    double modifier = monitor.getExperienceAmount() / UPDATE_FREQUENCY;
                    chanceRatio[i] += modifier;
                    monitor.setExperienceAmount(0);
                } else if ((chanceRatio[i] -= 5) < 0) {
                    chanceRatio[i] = 0;
                }
            }
        }
        nextPulse = World.getTicks() + UPDATE_FREQUENCY;
    }

    /**
     * Resets the trigger chance ratio.
     */
    public void resetTrigger() {
        for (int j = 0; j < chanceRatio.length; j++) {
            chanceRatio[j] = 0;
        }
    }

    /**
     * Gets the chance ratio.
     *
     * @param skillId The skill id.
     * @return The chance ratio.
     */
    public int getChanceRatio(int skillId) {
        return chanceRatio[skillId];
    }

    /**
     * Initializes the anti macro event handler.
     */
    public void init() {
        for (int i = 0; i < monitors.length; i++) {
            monitors[i] = new ExperienceMonitor(i);
        }
        nextPulse = World.getTicks() + UPDATE_FREQUENCY;
        if (event != null) {
            event.start(player, true);
        }
    }

    /**
     * Registers a new anti-macro event.
     *
     * @param event The event.
     */
    public static void register(AntiMacroEvent event) {
        event.register();
        EVENTS.put(event.getName(), event);
    }

    /**
     * Registers experience gain.
     *
     * @param skill      The skill id.
     * @param experience The experience gained.
     */
    public void registerExperience(int skill, double experience) {
        monitors[skill].setExperienceAmount((int) (monitors[skill].getExperienceAmount() + experience));
    }

    /**
     * Checks if the npc is part of the event.
     *
     * @param npc     the npc.
     * @param message the message.
     * @return <code>True</code> if so.
     */
    public boolean isNPC(NPC npc, boolean message) {
        if (!hasEvent()) {
            if (message) {
                player.getActionSender().sendMessage("They don't seem interested in talking to you.");
            }
            return false;
        }
        AntiMacroNPC n = (AntiMacroNPC) npc;
        if (n.getEvent() != event) {
            if (message) {
                player.getActionSender().sendMessage("They don't seem interested in talking to you.");
            }
            return false;
        }
        return true;
    }

    /**
     * Fires an anti-macro event.
     *
     * @param name The name of the event to start.
     * @param args The arguments.
     * @return <code>True</code> if the event has been fired.
     */
    public boolean fireEvent(String name, Object... args) {
        if (hasEvent() || player.getZoneMonitor().isRestricted(ZoneRestriction.RANDOM_EVENTS)) {
            return false;
        }
        AntiMacroEvent event = EVENTS.get(name);
        if (event == null) {
            if (World.getConfiguration().isDevelopmentEnabled()) {
                throw new IllegalArgumentException("Could not find event " + name + "!");
            } else {
                return false;
            }
        }
        event = event.create(player);
        if (!event.start(player, false, args)) {
            return false;
        }
        resetTrigger();
        this.event = event;
        return true;
    }

    /**
     * Fires an event.
     *
     * @param skillId The skill id, -1 for default events..
     * @param args    The arguments.
     * @return <code>True</code> if the event has been fired.
     */
    public boolean fireEvent(int skillId, Object... args) {
        if (hasEvent() || EVENTS.isEmpty() || player.getZoneMonitor().isRestricted(ZoneRestriction.RANDOM_EVENTS)) {
            return false;
        }
        event = getRandomEvent(skillId);
        if (event != null) {
            if ((event = event.create(player)).start(player, false, args)) {
                resetTrigger();
                return true;
            }
            event = null;
        }
        return false;
    }

    /**
     * Gets an anti marco event.
     *
     * @param skillId the skillId.
     * @return {@code AntiMacroEvent} the event.
     */
    public AntiMacroEvent getRandomEvent(int skillId) {
        int index = RandomUtil.random(EVENTS.size());
        int count = 0;
        AntiMacroEvent event;
        AntiMacroEvent[] events = EVENTS.values().toArray(new AntiMacroEvent[EVENTS.size()]);
        while (!(event = events[index]).canFire(skillId)) {
            if (count++ >= events.length) {
                event = null;
                break;
            }
            index = (index + 1) % events.length;
        }
        return event;
    }

    /**
     * Checks if the player has an anti-macro event running.
     *
     * @return <code>True</code> if so.
     */
    public boolean hasEvent() {
        return event != null && !event.isTerminated();
    }

    /**
     * Gets the player.
     *
     * @return The player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the event.
     *
     * @return The event.
     */
    public AntiMacroEvent getEvent() {
        return event;
    }

    /**
     * Sets the event.
     *
     * @param event The event to set.
     */
    public void setEvent(AntiMacroEvent event) {
        this.event = event;
    }
}
