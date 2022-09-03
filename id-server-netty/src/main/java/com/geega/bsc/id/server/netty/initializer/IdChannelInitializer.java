package com.geega.bsc.id.server.netty.initializer;

import com.geega.bsc.id.common.utils.SnowFlake;
import com.geega.bsc.id.server.netty.handler.IdChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 初始化处理器
 *
 * @author Jun.An3
 * @date 2022/09/02
 */
public class IdChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SnowFlake snowFlake;

    public IdChannelInitializer(SnowFlake snowFlake) {
        this.snowFlake = snowFlake;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(
                //请求时使用，解码（包含了最大的接受数据量是256byte，并且告知了长度头是4byte）
                new PacketDecoder(256, 4),
                //响应时使用，再经过编码器将该byte数组转成数据长度(4byte，表示256 * 256 * 256 * 256= 65536 * 65536 byte的支持长度) + byte数组
                new LengthFieldPrepender(4),
                //响应时使用，先经过encoder将数据转成byte数组)
                new PacketEncoder());
        //绑定处理器
        socketChannel.pipeline().addLast(new IdChannelHandler(snowFlake));
    }

}
