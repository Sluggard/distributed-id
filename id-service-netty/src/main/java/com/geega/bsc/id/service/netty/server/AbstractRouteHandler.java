package com.geega.bsc.id.service.netty.server;

import com.alibaba.fastjson.JSON;
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
        HttpRequestParser httpRequestParser = new HttpRequestParser(httpRequest);
        //打印日志
        log(httpRequestParser);
        //处理请求逻辑
        String response = handle(ctx, httpRequestParser);
        //请求响应
        response(ctx, response);
    }

    private void response(ChannelHandlerContext ctx, String response) {
        HttpUtils.response(ctx, response);
    }

    private void log(HttpRequestParser httpRequestParser) {
        log.info("uri：{}，method：{}，body：{}", httpRequestParser.path(), httpRequestParser.method().name(), JSON.toJSONString(httpRequestParser.bodyParams()));
    }

}
