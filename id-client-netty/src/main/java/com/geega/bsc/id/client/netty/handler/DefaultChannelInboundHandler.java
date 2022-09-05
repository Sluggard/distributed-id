package com.geega.bsc.id.client.netty.handler;

import com.geega.bsc.id.client.netty.client.IdClient;
import com.geega.bsc.id.client.netty.packet.Packet;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * @author Jun.An3
 * @date 2022/09/05
 */
@Slf4j
@ChannelHandler.Sharable
public class DefaultChannelInboundHandler extends ChannelInboundHandlerAdapter {

    private final IdClient idClient;

    private SocketChannel socketChannel;

    private final List<ConnectListener> connectListeners;

    public void send(Packet packet) {
        this.socketChannel.writeAndFlush(packet);
    }

    public DefaultChannelInboundHandler(IdClient idClient, final List<ConnectListener> connectListeners) {
        this.idClient = idClient;
        this.connectListeners = connectListeners;
    }

    public boolean isClosed(){
        return !socketChannel.isActive();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.socketChannel = (SocketChannel) ctx.channel();
        ctx.fireChannelActive();
        log.warn("连接建立：{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.fireChannelInactive();
        log.warn("连接断开：{}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.warn("捕获异常", cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet packet = (Packet) msg;
        idClient.cache(packet.getIds());
        log.info("接受数据：{}", new String(packet.getBody()));
    }

}
