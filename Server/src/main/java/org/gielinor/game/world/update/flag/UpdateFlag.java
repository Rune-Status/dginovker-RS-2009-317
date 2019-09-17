package org.gielinor.game.world.update.flag;

import org.gielinor.game.node.entity.Entity;
import org.gielinor.net.packet.PacketBuilder;

/**
 * Represents an update flag, which holds the data the update mask uses to write.
 *
 * @author Emperor
 */
public abstract class UpdateFlag<T> implements Comparable<UpdateFlag<?>> {

    /**
     * The context.
     */
    protected T context;

    /**
     * Constructs a new {@code UpdateFlag} {@code Object}.
     *
     * @param context The context.
     */
    public UpdateFlag(T context) {
        this.context = context;
    }

    /**
     * Writes the data on the buffer.
     *
     * @param buffer The buffer.
     */
    public abstract void write(PacketBuilder buffer);

    /**
     * Writes the data on the buffer.
     *
     * @param buffer The buffer.
     * @param e      The entity to write for.
     */
    public void writeDynamic(PacketBuilder buffer, Entity e) {
        write(buffer);
    }

    /**
     * Gets the update mask data for this update flag.
     *
     * @return The update mask data.
     */
    public abstract int data();

    /**
     * The ordinal.
     *
     * @return The update mask ordinal.
     */
    public abstract int ordinal();

    @Override
    public int compareTo(UpdateFlag<?> flag) {
        if (flag.ordinal() == ordinal()) {
            return 0;
        }
        if (flag.ordinal() < ordinal()) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UpdateFlag && ((UpdateFlag<?>) o).data() == data() && ((UpdateFlag<?>) o).ordinal() == ordinal();
    }

    /**
     * Get the context.
     *
     * @return the context
     */
    public T getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(T context) {
        this.context = context;
    }
}