package com.geega.bsc.id.common.network;

import com.geega.bsc.id.common.exception.InvalidReceiveException;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;

/**
 * @author Jun.An3
 * @date 2022/08/29
 */
public class NetworkReceive implements Receive {

    private final static int UNLIMITED = -1;

    private final String source;

    private final ByteBuffer size;

    private final int maxSize;

    private ByteBuffer buffer;

    NetworkReceive(int maxSize, String source) {
        this.source = source;
        this.size = ByteBuffer.allocate(4);
        this.buffer = null;
        this.maxSize = maxSize;
    }

    @Override
    public String source() {
        return source;
    }

    @Override
    public boolean complete() {
        return !size.hasRemaining() && !buffer.hasRemaining();
    }

    @Override
    public long readFrom(ScatteringByteChannel channel) throws IOException {
        return readFromReadableChannel(channel);
    }

    private long readFromReadableChannel(ReadableByteChannel channel) throws IOException {
        int read = 0;
        if (size.hasRemaining()) {
            int bytesRead = channel.read(size);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            read += bytesRead;
            if (!size.hasRemaining()) {
                size.rewind();
                int receiveSize = size.getInt();
                if (receiveSize < 0) {
                    throw new InvalidReceiveException("Invalid receive (size = " + receiveSize + ")");
                }
                if (maxSize != UNLIMITED && receiveSize > maxSize) {
                    throw new InvalidReceiveException("Invalid receive (size = " + receiveSize + " larger than " + maxSize + ")");
                }

                this.buffer = ByteBuffer.allocate(receiveSize);
            }
        }
        if (buffer != null) {
            int bytesRead = channel.read(buffer);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            read += bytesRead;
        }

        return read;
    }

    public ByteBuffer payload() {
        return this.buffer;
    }

}