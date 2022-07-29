package com.geega.bsc.id.common.network;

import cn.hutool.core.builder.HashCodeBuilder;
import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.utils.ByteUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.util.Objects;

/**
 * @author Jun.An3
 * @date 2022/07/18
 */
public class DistributedIdChannel {

    private final String id;

    private final TransportLayer transportLayer;

    private final int maxReceiveSize;

    private NetworkReceive receive;

    private Send send;

    public DistributedIdChannel(String id, TransportLayer transportLayer, int maxReceiveSize) {
        this.id = id;
        this.transportLayer = transportLayer;
        this.maxReceiveSize = maxReceiveSize;
    }

    public void close() throws IOException {
        ByteUtil.closeAll(transportLayer);
    }

    /**
     * Does handshake of transportLayer and authentication using configured authenticator
     */
    public void prepare() throws IOException {
        if (!transportLayer.ready()) {
            transportLayer.handshake();
        }
    }

    public void disconnect() {
        transportLayer.disconnect();
    }


    public boolean finishConnect() throws IOException {
        return transportLayer.finishConnect();
    }

    public boolean isConnected() {
        return transportLayer.isConnected();
    }

    public String id() {
        return id;
    }

    public void mute() {
        transportLayer.removeInterestOps(SelectionKey.OP_READ);
    }

    public void unmute() {
        transportLayer.addInterestOps(SelectionKey.OP_READ);
    }

    public boolean isMute() {
        return transportLayer.isMute();
    }

    public boolean ready() {
        return transportLayer.ready();
    }

    public boolean hasSend() {
        return send != null;
    }

    /**
     * Returns the address to which this channel's socket is connected or `null` if the socket has never been connected.
     * <p>
     * If the socket was connected prior to being closed, then this method will continue to return the
     * connected address after the socket is closed.
     */
    public InetAddress socketAddress() {
        return transportLayer.socketChannel().socket().getInetAddress();
    }

    public String socketDescription() {
        Socket socket = transportLayer.socketChannel().socket();
        if (socket.getInetAddress() == null) {
            return socket.getLocalAddress().toString();
        }
        return socket.getInetAddress().toString();
    }

    public void setSend(Send send, boolean skip) {
        if (this.send != null) {
            if (skip) {
                return;
            }
            throw new DistributedIdException("异常：上一个Send请求未完成，又开始Send请求了");
        }
        this.send = send;
        this.transportLayer.addInterestOps(SelectionKey.OP_WRITE);
    }

    public NetworkReceive read() throws IOException {
        NetworkReceive result = null;

        if (receive == null) {
            receive = new NetworkReceive(maxReceiveSize, id);
        }

        receive(receive);
        if (receive.complete()) {
            receive.payload().rewind();
            result = receive;
            receive = null;
        }
        return result;
    }

    public Send write() throws IOException {
        Send result = null;
        if (send != null && send(send)) {
            result = send;
            send = null;
        }
        return result;
    }

    private long receive(NetworkReceive receive) throws IOException {
        return receive.readFrom(transportLayer);
    }

    private boolean send(Send send) throws IOException {
        send.writeTo(transportLayer);
        if (send.completed()) {
            //写完数据，要去掉写事件
            transportLayer.removeInterestOps(SelectionKey.OP_WRITE);
        }
        return send.completed();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(this.id, ((DistributedIdChannel) o).id);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

}