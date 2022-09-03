package com.geega.bsc.id.server.netty.initializer;

import com.geega.bsc.id.server.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 网络包编码
 *
 * @author Jun.An3
 * @date 2021/11/15
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) {
        msg.write(out);
    }

}
