package com.geega.bsc.id.service.netty.server;

import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求参数解析器
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
public class HttpRequestParser {

    private final FullHttpRequest httpRequest;

    /**
     * 构造一个解析器
     */
    public HttpRequestParser(FullHttpRequest req) {
        this.httpRequest = req;
    }

    /**
     * 获取path
     * 例子：/api/v1/test
     */
    public String uri() {
        return httpRequest.uri();
    }

    /**
     * 获取uri
     * 例子：/api/v1/test?name=test
     */
    public String path() {
        return new QueryStringDecoder(httpRequest.uri()).uri();
    }

    /**
     * 获取http method
     */
    public HttpMethod method() {
        return httpRequest.method();
    }

    /**
     * 解析请求uri参数
     *
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     */
    public Map<String, Object> uriParams() {
        Map<String, Object> paramMap = new HashMap<>(4);
        QueryStringDecoder decoder = new QueryStringDecoder(httpRequest.uri());
        decoder.parameters().forEach((key, value) -> {
            // entry.getValue()是一个List, 只取第一个元素
            paramMap.put(key, value.get(0));
        });
        return paramMap;
    }

    /**
     * 解析请求body参数
     * 目前默认content-type=application/json，都认为json数据结构
     */
    public Map<String, Object> bodyParams() {
        Map<String, Object> paramMap = new HashMap<>(4);
        final ByteBuf content = httpRequest.content();
        if (content.capacity() != 0) {
            byte[] requestContent = new byte[content.readableBytes()];
            content.readBytes(requestContent);
            String strContent = new String(requestContent, StandardCharsets.UTF_8);
            JSONObject jsonParamRoot = JSONObject.parseObject(strContent);
            for (String key : jsonParamRoot.keySet()) {
                paramMap.put(key, jsonParamRoot.get(key));
            }
        }
        return paramMap;
    }

}