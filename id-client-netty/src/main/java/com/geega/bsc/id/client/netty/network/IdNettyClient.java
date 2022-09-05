package com.geega.bsc.id.client.netty.network;

import com.geega.bsc.id.client.netty.client.IdClient;
import com.geega.bsc.id.client.netty.handler.IdClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Jun.An3
 * @date 2022/09/04
 */
public class IdNettyClient {

    private final IdClient idClient;

    public IdNettyClient(IdClient idClient) {
        this.idClient = idClient;
    }

    public void start(String ip, int port) throws InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new IdClientChannelInitializer(idClient, null));

        ChannelFuture sync = bootstrap.connect(ip, port).sync();
        sync.channel().closeFuture().sync();

    }

}
