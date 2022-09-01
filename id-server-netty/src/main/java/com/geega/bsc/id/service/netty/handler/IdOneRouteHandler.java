package com.geega.bsc.id.service.netty.handler;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.service.netty.response.BizResult;
import com.geega.bsc.id.service.netty.server.AbstractRouteHandler;
import com.geega.bsc.id.service.netty.server.HttpRequestParser;
import io.netty.channel.ChannelHandlerContext;

/**
 * 获取一个id http请求 handler
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
public class IdOneRouteHandler extends AbstractRouteHandler {

    private final IdClient idClient;

    public IdOneRouteHandler(IdClient idClient) {
        this.idClient = idClient;
    }

    @Override
    public String handle(ChannelHandlerContext ctx, HttpRequestParser parser) {
        return JSON.toJSONString(BizResult.success(idClient.id(100)));
    }

}
