package com.geega.bsc.id.service.netty.handler;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.service.netty.response.BizResult;
import com.geega.bsc.id.service.netty.server.AbstractRouteHandler;
import com.geega.bsc.id.service.netty.server.HttpRequestParser;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取id数组 http请求 handler
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
@Slf4j
public class IdListRouteHandler extends AbstractRouteHandler {

    private final IdClient idClient;

    public IdListRouteHandler(IdClient idClient) {
        this.idClient = idClient;
    }

    @Override
    public String handle(ChannelHandlerContext ctx, HttpRequestParser parser) {
        Map<String, Object> uriParams = parser.uriParams();
        int numInt = Integer.parseInt((String) uriParams.getOrDefault("num", "10"));
        List<Long> result = new ArrayList<>(numInt);
        for (int i = 0; i < numInt; i++) {
            result.add(idClient.id(100));
        }
        log.info("生成ID：{}", JSON.toJSONString(result));
        return JSON.toJSONString(BizResult.success(result));
    }

}
