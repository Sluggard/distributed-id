/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.geega.bsc.id.server.netty.handler;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.common.utils.SnowFlake;
import com.geega.bsc.id.server.netty.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * IdChannelHandler
 *
 * @author Jun.An3
 * @date 2022/09/02
 */
@Slf4j
public class IdChannelHandler extends ChannelInboundHandlerAdapter {

    private final SnowFlake snowFlake;

    public IdChannelHandler(SnowFlake snowFlake) {
        this.snowFlake = snowFlake;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("连接已断开：{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet packet = (Packet) msg;
        int num = packet.getNum();
        List<Long> result = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            result.add(snowFlake.nextId());
        }
        String ids = JSON.toJSONString(result);
        log.info("生成ID：{}", ids);
        ctx.writeAndFlush(Packet.builder().body(ids.getBytes()).build());
    }

}
