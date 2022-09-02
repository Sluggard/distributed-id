package com.geega.bsc.id.service.netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;


/**
 * http请求处理器
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
@Slf4j
@ChannelHandler.Sharable
public class IdHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Map<String, AbstractRouteHandler> routeHandlerMap;

    public IdHttpServerHandler(Map<String, AbstractRouteHandler> routeHandlerMap) {
        this.routeHandlerMap = routeHandlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        final AbstractRouteHandler routeHandler = routeHandlerMap.get(HttpUtils.getUri(request));
        if (routeHandler == null) {
            //无对应请求逻辑
            noneRouteHandlerMsg(ctx);
        } else {
            //处理请求
            routeHandler.process(ctx, request);
        }
    }

    private void noneRouteHandlerMsg(ChannelHandlerContext ctx) {
        HttpUtils.response(ctx, "{\"code\":\"404\",\"msg\":\"无资源\",\"result\":null}");
    }

}
