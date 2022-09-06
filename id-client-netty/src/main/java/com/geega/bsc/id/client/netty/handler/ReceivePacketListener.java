package com.geega.bsc.id.client.netty.handler;

import com.geega.bsc.id.client.netty.packet.Packet;

/**
 * 接受数据监听器
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
public interface ReceivePacketListener {

    /**
     * 接受请求通知
     *
     * @param packet 数据包
     */
    void receive(Packet packet);

}
