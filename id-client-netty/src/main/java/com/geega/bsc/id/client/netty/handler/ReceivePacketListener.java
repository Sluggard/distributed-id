package com.geega.bsc.id.client.netty.handler;

import com.geega.bsc.id.client.netty.packet.Packet;

/**
 * 接受数据监听器
 *
 * @author Jun.An3l
 * @date 2021/11/22
 */
public interface ReceivePacketListener {

    void receive(Packet packet);

}
