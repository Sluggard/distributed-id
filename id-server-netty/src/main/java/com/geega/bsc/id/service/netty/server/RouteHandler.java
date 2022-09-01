package com.geega.bsc.id.service.netty.server;

import io.netty.channel.ChannelHandlerContext;

/**
 * 不同的uri 对应一个 处理逻辑类
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
public interface RouteHandler {

    /**
     * 不同的uri 对应一个 处理逻辑
     *
     * @param parser 请求参数对象(可以获取uri，uri param，method，body param)
     * @param ctx    channel context
     * @return 响应数据，目前就是json string，例子 -> {"code":"404","msg":"该请求无对应处理器","result":null}
     */
    String handle(ChannelHandlerContext ctx, HttpRequestParser parser);

}
