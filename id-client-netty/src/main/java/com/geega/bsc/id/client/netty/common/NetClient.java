package com.geega.bsc.id.client.netty.common;

import com.geega.bsc.id.client.netty.handler.DefaultChannelInboundHandler;
import com.geega.bsc.id.client.netty.handler.PacketDecoder;
import com.geega.bsc.id.client.netty.handler.PacketEncoder;
import com.geega.bsc.id.client.netty.packet.Packet;
import com.geega.bsc.id.common.utils.ByteBufferUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 网络客户端
 *
 * @author Jun.An3
 * @date 2022/09/05
 */
public class NetClient {

    private DefaultChannelInboundHandler defaultChannelInboundHandler;

    public NetClient(String ip, int port) {
        this.defaultChannelInboundHandler = new DefaultChannelInboundHandler();

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(
                        //服务端 -> 客户端，请求时使用，解码（包含了最大的接受数据量是2 * 1024byte，并且告知了长度头是4byte）
                        new PacketDecoder(2 * 1024, 4),
                        //客户端 -> 服务端，响应时使用，再经过编码器将该byte数组转成数据长度(4byte，表示256 * 256 * 256 * 256= 65536 * 65536 byte的支持长度) + byte数组
                        new LengthFieldPrepender(4),
                        //客户端 -> 服务端，响应时使用，先经过encoder将数据转成byte数组)
                        new PacketEncoder());
                //接受数据
                socketChannel.pipeline().addLast(defaultChannelInboundHandler);
            }
        });

        ChannelFuture sync = bootstrap.connect(ip, port).sync();
        sync.channel().closeFuture().sync();
    }

    /**
     * 发送消息
     *
     * @param num 数量
     */
    public void send(int num) {
        Packet packet = Packet.builder().body(ByteBufferUtil.intToByte(num)).build();
        defaultChannelInboundHandler.send(packet);
    }

    /**
     * 连接是否有效
     */
    public boolean isClosed() {
        return !defaultChannelInboundHandler.isClosed();
    }

    public void close() {

    }

}
