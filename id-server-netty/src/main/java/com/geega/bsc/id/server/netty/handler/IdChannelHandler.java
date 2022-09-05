/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.geega.bsc.id.server.netty.handler;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.common.utils.AddressUtil;
import com.geega.bsc.id.common.utils.SnowFlake;
import com.geega.bsc.id.server.netty.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
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

    private SocketChannel socketChannel;

    private final SnowFlake snowFlake;

    public IdChannelHandler(SnowFlake snowFlake) {
        this.snowFlake = snowFlake;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.socketChannel = (SocketChannel) ctx.channel();
        ctx.fireChannelActive();
        log.warn("连接建立：{}", getConnectionId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
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

    public String getConnectionId() {
        InetSocketAddress localAddress = socketChannel.localAddress();
        InetSocketAddress remoteAddress = socketChannel.remoteAddress();
        return AddressUtil.getConnectionId(localAddress, remoteAddress);
    }

}
