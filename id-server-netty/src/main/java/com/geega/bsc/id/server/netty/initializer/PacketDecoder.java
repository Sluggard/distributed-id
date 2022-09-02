package com.geega.bsc.id.server.netty.initializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 网络包编码
 *
 * @author Jun.An3
 * @date 2021/11/15
 */
public class PacketDecoder extends LengthFieldBasedFrameDecoder {

    public PacketDecoder(int maxFrameLength) {
        super(maxFrameLength, 0,
                3, 0, 3);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, buffer);

        return null;
    }

}
