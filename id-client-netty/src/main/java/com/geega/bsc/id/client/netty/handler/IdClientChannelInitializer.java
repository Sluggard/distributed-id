package com.geega.bsc.id.client.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

public class IdClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(
                //服务端 -> 客户端，请求时使用，解码（包含了最大的接受数据量是256byte，并且告知了长度头是4byte）
                new PacketDecoder(2 * 1024, 4),
                //客户端 -> 服务端，响应时使用，数据处理器（发送num）

                //客户端 -> 服务端，响应时使用，再经过编码器将该byte数组转成数据长度(4byte，表示256 * 256 * 256 * 256= 65536 * 65536 byte的支持长度) + byte数组
                new LengthFieldPrepender(4),
                //客户端 -> 服务端，响应时使用，先经过encoder将数据转成byte数组)
                new PacketEncoder());

        socketChannel.pipeline().addLast(new IdClientReadChannelHandler());
    }

}
