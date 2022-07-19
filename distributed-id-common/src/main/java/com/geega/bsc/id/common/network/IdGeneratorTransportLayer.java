package com.geega.bsc.id.common.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author Jun.An3
 * @date 2022/07/18
 */
public class IdGeneratorTransportLayer implements TransportLayer {

    private final SelectionKey key;

    private final SocketChannel socketChannel;

    public IdGeneratorTransportLayer(SelectionKey key) {
        this.key = key;
        this.socketChannel = (SocketChannel) key.channel();
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public boolean finishConnect() throws IOException {
        boolean connected = this.socketChannel.finishConnect();
        if (connected) {
            this.key.interestOps(this.key.interestOps() & -9 | 1);
        }

        return connected;
    }

    @Override
    public void disconnect() {
        this.key.cancel();
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
    public boolean isConnected() {
        return this.socketChannel.isConnected();
    }

    @Override
    public void close() throws IOException {
        try {
            this.socketChannel.socket().close();
            this.socketChannel.close();
        } finally {
            this.key.attach((Object) null);
            this.key.cancel();
        }

    }

    @Override
    public void handshake() throws IOException {
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
    public boolean hasPendingWrites() {
        return false;
    }

    @Override
    public void addInterestOps(int ops) {
        this.key.interestOps(this.key.interestOps() | ops);
    }

    @Override
    public void removeInterestOps(int ops) {
        this.key.interestOps(this.key.interestOps() & ~ops);
    }

    @Override
    public boolean isMute() {
        return this.key.isValid() && (this.key.interestOps() & 1) == 0;
    }

    @Override
    public long transferFrom(FileChannel fileChannel, long position, long count) throws IOException {
        return fileChannel.transferTo(position, count, this.socketChannel);
    }

}