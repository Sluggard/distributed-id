package com.geega.bsc.id.service.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import java.util.Map;

/**
 * channel初始化器
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
public class IdChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Map<String, AbstractRouteHandler> routeHandlerMap;

    public IdChannelInitializer(Map<String, AbstractRouteHandler> routeHandlerMap) {
        this.routeHandlerMap = routeHandlerMap;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        final ChannelPipeline pipeline = ch.pipeline();
        /*
         * 解析成HttpRequest
         * HttpServerCodec只能获取uri中参数
         */
        pipeline.addLast(new HttpServerCodec());
        /*
         * 解析成FullHttpRequest
         * HttpObjectAggregator解析http request body消息
         */
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
        /*
         * 自定义的http请求逻辑处理器
         */
        pipeline.addLast(new IdHttpServerHandler(routeHandlerMap));
    }

}
