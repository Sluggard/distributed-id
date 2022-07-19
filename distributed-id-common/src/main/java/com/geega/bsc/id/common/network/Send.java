package com.geega.bsc.id.common.network;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;

/**
 * This interface models the in-progress sending of data to a destination identified by an integer id.
 */
public interface Send {

    /**
     * The numeric id for the destination of this send
     */
    public String destination();

    /**
     * Is this send complete?
     */
    public boolean completed();

    /**
     * Write some as-yet unwritten bytes from this send to the provided channel. It may take multiple calls for the send
     * to be completely written
     *
     * @param channel The Channel to write to
     * @return The number of bytes written
     * @throws IOException If the write fails
     */
    public long writeTo(GatheringByteChannel channel) throws IOException;

    /**
     * Size of the send
     */
    public long size();

}