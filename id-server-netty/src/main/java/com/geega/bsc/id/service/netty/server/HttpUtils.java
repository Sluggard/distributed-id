package com.geega.bsc.id.service.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

/**
 * 专门构造json数据格式
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
public class HttpUtils {

    /**
     * 获取http方法
     * post，get，put，delete
     */
    public static String getUri(HttpRequest httpRequest) {
        return new QueryStringDecoder(httpRequest.uri()).path();
    }

    /**
     * 响应请求
     */
    public static void response(ChannelHandlerContext ctx, String response) {
        assert response != null;
        ByteBuf byteBuf = Unpooled.copiedBuffer(response, CharsetUtil.UTF_8);
        // 构造一个http响应体，即HttpResponse
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        // 设置响应头信息
        defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        // 将响应体写入到通道中
        ctx.writeAndFlush(defaultFullHttpResponse);
    }

}
