package com.geega.bsc.id.common.network;

import com.geega.bsc.id.common.utils.AddressUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 传输层
 *
 * @author Jun.An3
 * @date 2022/07/18
 */
@Slf4j
public class IdGeneratorTransportLayer implements TransportLayer {

    private final SelectionKey key;

    private final SocketChannel socketChannel;

    private final String connectionId;

    public IdGeneratorTransportLayer(SelectionKey key) {
        this.key = key;
        this.socketChannel = (SocketChannel) key.channel();
        this.connectionId = AddressUtil.getConnectionId(socketChannel);
        log.info("创建连接：[{}]", connectionId);
    }

    @Override
    public String getConnectionId() {
        return this.connectionId;
    }

    private boolean keyIsValid() {
        return this.key.isValid();
    }

    @Override
    public boolean finishConnect() throws IOException {
        boolean connected = this.socketChannel.finishConnect();
        if (connected) {
            //建立连接后，关注read事件，移除accept、write事件
            this.key.interestOps(this.key.interestOps() & -9 | SelectionKey.OP_READ);
        }
        return connected;
    }

    @Override
    public SocketChannel socketChannel() {
        return this.socketChannel;
    }

    @Override
    public boolean isOpen() {
        return this.socketChannel.isOpen();
    }

    @Override
    public void close() throws IOException {
        try {
            this.socketChannel.socket().close();
            this.socketChannel.close();
        } finally {
            this.key.attach(null);
            this.key.cancel();
        }

    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return this.socketChannel.read(dst);
    }

    @Override
    public long read(ByteBuffer[] dsts) throws IOException {
        return this.socketChannel.read(dsts);
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        return this.socketChannel.read(dsts, offset, length);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return this.socketChannel.write(src);
    }

    @Override
    public long write(ByteBuffer[] srcs) throws IOException {
        return this.socketChannel.write(srcs);
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        return this.socketChannel.write(srcs, offset, length);
    }

    @Override
    public void addInterestOps(int ops) {
        if (keyIsValid()) {
            this.key.interestOps(this.key.interestOps() | ops);
        }
    }

    @Override
    public void removeInterestOps(int ops) {
        if (keyIsValid()) {
            this.key.interestOps(this.key.interestOps() & ~ops);
        }
    }

    @Override
    public boolean isMute() {
        return this.key.isValid() && (this.key.interestOps() & 1) == 0;
    }

}