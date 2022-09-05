package com.geega.bsc.id.server.netty.initializer;

import com.geega.bsc.id.server.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 网络包编码
 *
 * @author Jun.An3
 * @date 2021/11/15
 */
@Slf4j
public class PacketDecoder extends LengthFieldBasedFrameDecoder {

    public PacketDecoder(int maxFrameLength,int length) {
        super(maxFrameLength, 0, length, 0, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, buffer);
        if (byteBuf != null) {
            try {
                return Packet.parse(byteBuf);
            } finally {
                ReferenceCountUtil.release(byteBuf);
            }
        }
        return null;
    }

}
