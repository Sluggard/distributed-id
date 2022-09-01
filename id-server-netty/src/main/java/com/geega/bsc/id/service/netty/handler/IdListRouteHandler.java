package com.geega.bsc.id.service.netty.handler;

import com.geega.bsc.id.service.netty.server.AbstractRouteHandler;
import com.geega.bsc.id.service.netty.server.HttpRequestParser;
import io.netty.channel.ChannelHandlerContext;

/**
 * 获取id数组 http请求 handler
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
public class IdListRouteHandler extends AbstractRouteHandler {

    @Override
    public String handle(ChannelHandlerContext ctx, HttpRequestParser parser) {
        return null;
    }

}
