package com.geega.bsc.id.service.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象处理类
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
@Slf4j
public abstract class AbstractRouteHandler implements RouteHandler {

    void process(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        //打印日志
        log(httpRequest);
        //处理请求逻辑
        String response = handle(ctx, new HttpRequestParser(httpRequest));
        //请求响应
        response(ctx, response);
    }

    private void response(ChannelHandlerContext ctx, String response) {
        HttpUtils.response(ctx, response);
    }

    private void log(FullHttpRequest httpRequest) {
        log.info("请求uri：{}，请求method：{}，请求body：{}", httpRequest.uri(), httpRequest.method().name(), httpRequest.decoderResult());
    }

}
