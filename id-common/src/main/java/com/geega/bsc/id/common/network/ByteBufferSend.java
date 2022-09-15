package com.geega.bsc.id.common.network;

import com.geega.bsc.id.common.exception.DistributedIdException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

/**
 * buffers用于装数据
 * remaining主要是用于标识是否已经发送完成
 *
 * @author Jun.An3
 * @date 2022/07/18
 */
public class ByteBufferSend implements Send {

    private final ByteBuffer[] buffers;

    private int remaining;

    public ByteBufferSend(String id, ByteBuffer... buffers) {
        this.buffers = buffers;
        for (ByteBuffer buffer : buffers) {
            remaining += buffer.remaining();
        }
    }

    @Override
    public boolean completed() {
        //判断数据是否全部写完
        return remaining <= 0;
    }

    @Override
    public long writeTo(GatheringByteChannel channel) throws IOException {
        long written = channel.write(buffers);
        if (written < 0) {
            throw new DistributedIdException("写负数，不可能发生");
        }
        remaining -= written;
        return written;
    }

}