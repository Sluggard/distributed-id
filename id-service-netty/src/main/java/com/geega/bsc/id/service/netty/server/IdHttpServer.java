package com.geega.bsc.id.service.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

/**
 * http服务器
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
@Slf4j
public class IdHttpServer {

    private int port;

    private Map<String, AbstractRouteHandler> routeHandlerMap;

    @SuppressWarnings("unused")
    private IdHttpServer() {

    }

    public IdHttpServer(int port, Map<String, AbstractRouteHandler> routeHandlerMap) {
        this.port = port;
        this.routeHandlerMap = routeHandlerMap;
    }

    public void start() {
        NioEventLoopGroup bossGroup = null;
        NioEventLoopGroup workerGroup = null;

        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new IdChannelInitializer(routeHandlerMap));

            final ChannelFuture sync = bootstrap.bind(this.port).sync();
            sync.channel().closeFuture().sync();

        } catch (Exception e) {
            throw new RuntimeException("服务启动异常", e);
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
