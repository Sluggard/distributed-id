package com.geega.bsc.id.server.network;

import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.server.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author Jun.An3
 */
@Slf4j
public class ServerAcceptor extends Thread {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private ServerProcessor[] processors;

    private final ServerConfig serverConfig;

    private final ServerRequestCache requestChannel;

    public ServerAcceptor(ServerRequestCache requestChannel, ServerConfig serverConfig) {
        this.requestChannel = requestChannel;
        this.serverConfig = serverConfig;
        this.init();
    }

    private void init() {
        try {
            //初始化处理器
            this.initProcessor();
            //初始化网络Acceptor
            this.selector = Selector.open();
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.socket().setReceiveBufferSize(4096);
            this.serverSocketChannel.bind(new InetSocketAddress(this.serverConfig.getIp(), this.serverConfig.getPort()));
        } catch (Exception e) {
            throw new DistributedIdException("初始化网络错误", e);
        }
    }

    private void initProcessor() {
        int processors = serverConfig.getProcessor();
        ServerProcessor[] serverProcessors = new ServerProcessor[processors];
        for (int i = 0; i < processors; i++) {
            serverProcessors[i] = new ServerProcessor(i, this.requestChannel);
            serverProcessors[i].start();
        }
        this.processors = serverProcessors;
    }

    @Override
    public void run() {
        int currentProcessor = 0;
        try {
            this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                int select = selector.select();
                if (select > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKey.isAcceptable()) {
                            if (currentProcessor >= processors.length) {
                                currentProcessor = 0;
                            }
                            ServerProcessor processor = processors[currentProcessor++];
                            try {
                                SocketChannel socketChannel;
                                ServerSocketChannel serverSocketChannelTemp = (ServerSocketChannel) selectionKey.channel();
                                socketChannel = serverSocketChannelTemp.accept();

                                socketChannel.configureBlocking(false);
                                socketChannel.socket().setTcpNoDelay(true);
                                socketChannel.socket().setKeepAlive(true);
                                socketChannel.socket().setReceiveBufferSize(1024);
                                socketChannel.socket().setSendBufferSize(2 * 1024);
                                processor.addChannel(socketChannel);
                            } catch (Exception e) {
                                log.error("无法创建连接", e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("网络异常", e);
            }
        }
    }

}
