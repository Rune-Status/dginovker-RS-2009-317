package org.gielinor.game.content.skill;

import org.gielinor.game.content.skill.free.gather.SkillingResource;
import org.gielinor.game.content.skill.free.gather.SkillingTool;
import org.gielinor.game.node.Node;
import org.gielinor.game.node.entity.impl.Animator.Priority;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.game.world.update.flag.context.Animation;
import org.gielinor.rs2.pulse.Pulse;

/**
 * Pulse class specifically used for Skills.
 *
 * @author Emperor
 */
public abstract class SkillPulse<T extends Node> extends Pulse {

    /**
     * The player.
     */
    protected final Player player;

    /**
     * The node.
     */
    protected T node;

    /**
     * The tool used.
     */
    protected SkillingTool tool;

    /**
     * The resource.
     */
    protected SkillingResource resource;

    /**
     * If we should reset the anim at the end.
     */
    protected boolean resetAnimation = true;

    /**
     * Constructs a new {@code SkillPulse} {@code Object}.
     *
     * @param player The player.
     * @param node   The node.
     */
    public SkillPulse(Player player, T node) {
        super(1, player, node);
        this.player = player;
        this.node = node;
        super.stop();
    }

    @Override
    public void start() {
        if (checkRequirements()) {
            super.start();
            message(0);
        }
    }

    @Override
    public boolean pulse() {
        if (!checkRequirements()) {
            return true;
        }
        animate();
        return reward();
    }

    @Override
    public void stop() {
        if (resetAnimation) {
            player.animate(new Animation(-1, Priority.HIGH));
        }
        super.stop();
        message(1);
    }

    /**
     * Checks if the player meets all the requirements.
     *
     * @return <code>True</code> if the player can continue, <code>False</code> if not.
     */
    public abstract boolean checkRequirements();

    /**
     * Sends the animations related to the actions the player is doing.
     */
    public abstract void animate();

    /**
     * Rewards the player.
     *
     * @return <code>True</code> if the player should stop this skilling pulse.
     */
    public abstract boolean reward();

    /**
     * Sends a message to the player.
     *
     * @param type The message type (0: start message, 1: stop message).
     */
    public void message(int type) {

    }

}
