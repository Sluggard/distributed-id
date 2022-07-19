package com.geega.bsc.id.server.network;

import com.geega.bsc.id.server.config.ServerConfig;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author Jun.An3
 */
public class ServerAcceptor extends Thread {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private final ServerProcessor[] processors;

    private final ServerConfig serverConfig;

    public ServerAcceptor(ServerProcessor[] processors, ServerConfig serverConfig) {
        this.processors = processors;
        this.serverConfig = serverConfig;
        this.init();
    }

    private void init() {
        try {

            this.selector = Selector.open();
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(this.serverConfig.getIp(), this.serverConfig.getPort()));
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.socket().setReceiveBufferSize(1024);

            for (ServerProcessor processor : this.processors) {
                processor.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int currentProcessor = 0;
        while (true) {
            try {
                this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                int select = selector.select(500);
                if (select > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKey.isAcceptable()) {
                            if (currentProcessor >= processors.length) {
                                currentProcessor = 0;
                            }

                            System.err.println("选择了处理器:" + currentProcessor);
                            ServerProcessor processor = selectProcessor(currentProcessor++);

                            try {
                                ServerSocketChannel serverSocketChannelTemp = (ServerSocketChannel) selectionKey.channel();
                                SocketChannel socketChannel = serverSocketChannelTemp.accept();
                                socketChannel.configureBlocking(false);
                                socketChannel.socket().setTcpNoDelay(true);
                                socketChannel.socket().setKeepAlive(true);
                                socketChannel.socket().setSendBufferSize(1024);

                                processor.addChannel(socketChannel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ServerProcessor selectProcessor(int currentProcessor) {
        return processors[currentProcessor];
    }

}
