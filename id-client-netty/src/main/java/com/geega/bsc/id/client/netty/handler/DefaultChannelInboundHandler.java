package com.geega.bsc.id.client.netty.handler;

import com.geega.bsc.id.client.netty.packet.Packet;
import com.geega.bsc.id.common.utils.AddressUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;

/**
 * @author Jun.An3
 * @date 2022/09/05
 */
@Slf4j
@ChannelHandler.Sharable
public class DefaultChannelInboundHandler extends ChannelInboundHandlerAdapter {

    private SocketChannel socketChannel;

    private final ConnectListener connectListener;

    private final ReceivePacketListener receivePacketListener;

    public void send(Packet packet) {
        this.socketChannel.writeAndFlush(packet);
    }

    public DefaultChannelInboundHandler(ConnectListener connectListener, ReceivePacketListener receivePacketListener) {
        this.connectListener = connectListener;
        this.receivePacketListener = receivePacketListener;
    }

    public boolean isClosed() {
        return !socketChannel.isActive();
    }

    public String getConnectionId() {
        InetSocketAddress localAddress = socketChannel.localAddress();
        InetSocketAddress remoteAddress = socketChannel.remoteAddress();
        return AddressUtil.getConnectionId(localAddress, remoteAddress);
    }

    public SocketChannel socketChannel() {
        return this.socketChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.socketChannel = (SocketChannel) ctx.channel();
        connectListener.statusChanged(true);
        ctx.fireChannelActive();
        log.warn("连接建立：{}", getConnectionId());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        connectListener.statusChanged(false);
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
        receivePacketListener.receive(packet);
    }

}
