package com.geega.bsc.id.client.netty.common;

import com.geega.bsc.id.client.netty.client.IdClient;
import com.geega.bsc.id.client.netty.handler.ConnectListener;
import com.geega.bsc.id.client.netty.handler.DefaultChannelInboundHandler;
import com.geega.bsc.id.client.netty.handler.PacketDecoder;
import com.geega.bsc.id.client.netty.handler.PacketEncoder;
import com.geega.bsc.id.client.netty.handler.ReceivePacketListener;
import com.geega.bsc.id.client.netty.packet.Packet;
import com.geega.bsc.id.client.netty.zk.ZkClient;
import com.geega.bsc.id.common.address.ServerNode;
import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.utils.ByteBufferUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 网络客户端
 *
 * @author Jun.An3
 * @date 2022/09/05
 */
@Slf4j
public class NetClient {

    private final ZkClient zkClient;

    private final IdClient idClient;

    private final DefaultChannelInboundHandler defaultChannelInboundHandler;

    private final NioEventLoopGroup bootGroup;

    public NetClient(ServerNode serverNode, ZkClient zkClient, IdClient idClient) {
        this.zkClient = zkClient;
        this.idClient = idClient;
        this.defaultChannelInboundHandler = new DefaultChannelInboundHandler(getConnectionListener(), getReceivePacketListener());
        try {
            bootGroup = new NioEventLoopGroup(1);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bootGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 10)
                    .option(ChannelOption.SO_SNDBUF, 1024)
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                    .option(ChannelOption.SO_BACKLOG, 10)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
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
            ChannelFuture sync = bootstrap.connect(serverNode.getIp(), serverNode.getPort()).sync();
            sync.channel().closeFuture().addListener((ChannelFutureListener) future -> bootGroup.shutdownGracefully());
        } catch (Exception e) {
            throw new DistributedIdException("客户端启动异常", e);
        }
    }

    private ConnectListener getConnectionListener() {
        return connected -> {
            //建立连接后，向zk注册客户端信息
            if (connected) {
                zkClient.register(defaultChannelInboundHandler.getConnectionId());
            }
        };
    }

    private ReceivePacketListener getReceivePacketListener() {
        return packet -> idClient.cache(packet.getIds());
    }

    /**
     * 发送消息
     *
     * @param num 数量
     */
    public void poll(int num) {
        try {
            Packet packet = Packet.builder()
                    .body(ByteBufferUtil.intToByte(num))
                    .build();
            if (defaultChannelInboundHandler.isActive()) {
                defaultChannelInboundHandler.send(packet);
            } else {
                log.error("发送数据异常");
            }
        } catch (Exception e) {
            log.error("发送数据异常");
        }

    }

    /**
     * 连接是否有效
     */
    public boolean isClosed() {
        return !defaultChannelInboundHandler.isActive();
    }

    /**
     * 关闭该连接的所有资源
     */
    public void close() {
        try {
            bootGroup.shutdownGracefully();
        } catch (Exception ignore) {
            //do nothing
        }
    }

}
