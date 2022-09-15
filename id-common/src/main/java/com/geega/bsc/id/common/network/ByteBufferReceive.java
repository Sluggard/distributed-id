package com.geega.bsc.id.common.network;

import com.geega.bsc.id.common.exception.InvalidReceiveException;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;

/**
 * 主要逻辑：
 * 先接受size大小的缓冲区，当缓冲区满了后，读取size bytebuffer 的int值
 * 这个int值就是数据body的byte数，再创建一个size bytebuffer中数据的length bytes bytebuffer，
 * 用于接收实际数据
 *
 * 是否接受完一个完整的数据包，主要是通过判断size和body是否已经装满
 * 如果没有缓冲满，直接在下一次读写事件到来时，继续读写
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
public class ByteBufferReceive implements Receive {

    private final static int UNLIMITED = -1;

    private final String source;

    private final ByteBuffer size;

    private final int maxSize;

    private ByteBuffer body;

    ByteBufferReceive(int maxSize, String source) {
        this.source = source;
        this.size = ByteBuffer.allocate(4);
        this.body = null;
        this.maxSize = maxSize;
    }

    @Override
    public String source() {
        return source;
    }

    @Override
    public boolean complete() {
        return !size.hasRemaining() && !body.hasRemaining();
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
                    throw new InvalidReceiveException("接受数据量：" + receiveSize);
                }
                if (maxSize != UNLIMITED && receiveSize > maxSize) {
                    throw new InvalidReceiveException("接受数据量：" + receiveSize + "，最大缓冲区： " + maxSize + ")");
                }
                this.body = ByteBuffer.allocate(receiveSize);
            }
        }
        if (body != null) {
            int bytesRead = channel.read(body);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            read += bytesRead;
        }
        return read;
    }

    public ByteBuffer payload() {
        return this.body;
    }

}