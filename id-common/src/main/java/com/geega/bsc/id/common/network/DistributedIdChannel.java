package com.geega.bsc.id.common.network;

import cn.hutool.core.builder.HashCodeBuilder;
import com.geega.bsc.id.common.utils.ByteUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Objects;

/**
 * @author Jun.An3
 * @date 2022/07/18
 */
@Slf4j
public class DistributedIdChannel {

    private final String id;

    private final TransportLayer transportLayer;

    private final int maxReceiveSize;

    private ByteBufferReceive receive;

    private Send send;

    public DistributedIdChannel(TransportLayer transportLayer, int maxReceiveSize) {
        this.id = transportLayer.getConnectionId();
        this.transportLayer = transportLayer;
        this.maxReceiveSize = maxReceiveSize;
    }

    public void close() throws IOException {
        ByteUtil.closeAll(transportLayer);
    }

    public boolean finishConnect() throws IOException {
        return transportLayer.finishConnect();
    }

    public String id() {
        return id;
    }

    public void removeConnectionEvent() {
        this.transportLayer.removeInterestOps(SelectionKey.OP_CONNECT);
    }

    public void interestReadEvent() {
        this.transportLayer.addInterestOps(SelectionKey.OP_READ);
    }

    public boolean isNotMute() {
        return !transportLayer.isMute();
    }

    public boolean setSend(Send send) {
        if (this.send != null) {
            return false;
        }
        this.send = send;
        this.transportLayer.addInterestOps(SelectionKey.OP_WRITE);
        return true;
    }

    public ByteBufferReceive read() throws IOException {
        ByteBufferReceive result = null;
        if (receive == null) {
            receive = new ByteBufferReceive(maxReceiveSize, id);
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

    private void receive(ByteBufferReceive receive) throws IOException {
        receive.readFrom(transportLayer);
    }

    private boolean send(Send send) throws IOException {
        send.writeTo(transportLayer);
        if (send.completed()) {
            //写完数据，要去掉写事件，因为nio是水平触发，如果不取消写事件，会一直接收到write ready notification
            //写完数据，取消write事件，添加读事件，有写数据时，就添加write事件；读取完数据，取消read事件；
            //但是这里不需要取消读事件，一个客户端只创建了一个连接，有数据时，就一直读取，未读完，下一次select时，读事件
            //还是有的，继续读取
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