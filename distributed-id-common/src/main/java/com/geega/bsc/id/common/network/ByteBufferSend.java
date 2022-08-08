package com.geega.bsc.id.common.network;

import com.geega.bsc.id.common.exception.DistributedIdException;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

/**
 * @author Jun.An3
 * @date 2022/07/18
 */
public class ByteBufferSend implements Send {

    private final String destination;

    private final int size;

    protected final ByteBuffer[] buffers;

    private int remaining;

    private boolean pending = false;

    public ByteBufferSend(String destination, ByteBuffer... buffers) {
        super();
        this.destination = destination;
        this.buffers = buffers;
        for (ByteBuffer buffer : buffers) {
            remaining += buffer.remaining();
        }
        this.size = remaining;
    }

    @Override
    public String destination() {
        return destination;
    }

    @Override
    public boolean completed() {
        //判断数据是否全部写完
        return remaining <= 0 && !pending;
    }

    @Override
    public long size() {
        return this.size;
    }

    @Override
    public long writeTo(GatheringByteChannel channel) throws IOException {
        long written = channel.write(buffers);
        if (written < 0) {
            throw new DistributedIdException("写负数，不可能发生");
        }
        remaining -= written;
        if (channel instanceof TransportLayer) {
            pending = ((TransportLayer) channel).hasPendingWrites();
        }
        return written;
    }

}