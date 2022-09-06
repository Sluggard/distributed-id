package com.geega.bsc.id.server.netty.server;

import com.geega.bsc.id.common.utils.SnowFlake;
import com.geega.bsc.id.server.netty.initializer.IdChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * http服务器
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
@Slf4j
public class IdTcpServer {

    private int port;

    private SnowFlake snowFlake;

    @SuppressWarnings("unused")
    private IdTcpServer() {

    }

    public IdTcpServer(int port, SnowFlake snowFlake) {
        this.port = port;
        this.snowFlake = snowFlake;
    }

    public void start() {
        NioEventLoopGroup bossGroup = null;
        NioEventLoopGroup workerGroup = null;

        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(1);

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new IdChannelInitializer(snowFlake));

            final ChannelFuture sync = bootstrap.bind(this.port).sync();
            sync.channel().closeFuture().sync();

        } catch (Exception e) {
            throw new RuntimeException("低代码插件启动异常", e);
        } finally {
            try {
                if (workerGroup != null) {
                    workerGroup.shutdownGracefully();
                }
                if (bossGroup != null) {
                    bossGroup.shutdownGracefully();
                }
            } catch (Exception ignored) {
            }
        }

    }

}
