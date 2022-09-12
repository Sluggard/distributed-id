package com.geega.bsc.id.server.network;

import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.network.ByteBufferReceive;
import com.geega.bsc.id.common.network.DistributedIdChannel;
import com.geega.bsc.id.common.network.IdGeneratorTransportLayer;
import com.geega.bsc.id.common.utils.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Jun.An3
 * @date 2022/07/18
 */
@Slf4j
public class ServerProcessor extends Thread {

    /**
     * key = localip:localpport-remoteip:remoteport
     * value = SocketChannel
     */
    private final ConcurrentHashMap<String, DistributedIdChannel> channels;

    private final Map<DistributedIdChannel, Deque<ByteBufferReceive>> completedReceives;

    /**
     * 从ServerAcceptor中来的数据
     */
    private final ConcurrentLinkedQueue<SocketChannel> newConnections;

    /**
     * 待关闭的连接
     */
    private final ConcurrentLinkedQueue<DistributedIdChannel> waitCloseConnections;

    private final ServerRequestCache requestChannel;

    private Selector selector;

    private final int processorId;

    public ServerProcessor(int processorId, ServerRequestCache requestChannel) {
        this.processorId = processorId;
        this.requestChannel = requestChannel;
        this.channels = new ConcurrentHashMap<>();
        this.newConnections = new ConcurrentLinkedQueue<>();
        this.waitCloseConnections = new ConcurrentLinkedQueue<>();
        this.completedReceives = new HashMap<>();
        this.init();
    }

    private void init() {
        try {
            this.selector = Selector.open();
        } catch (Exception e) {
            log.error("Selector.open()异常", e);
        }
    }

    public void addChannel(SocketChannel channel) {
        this.newConnections.add(channel);
        this.selector.wakeup();
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                //从队列中拉取数据出来,完成selector注册read事件
                configureNewConnections();
                //处理数据,将数据发送出去,就是写
                processNewResponses();
                //处理事件(读，写)
                handleEvent();
                //将读取完成的数据放入请求handler队列中
                handleCompletedReceives();
                //处理待关闭连接
                handleDisconnected();
            } catch (Exception e) {
                log.error("未知异常", e);
            }
        }
    }

    private void handleEvent() {
        try {
            final int select = selector.select();
            if (select > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    DistributedIdChannel distributedIdChannel = (DistributedIdChannel) selectionKey.attachment();
                    try {
                        if (selectionKey.isConnectable()) {
                            if (!distributedIdChannel.finishConnect()) {
                                continue;
                            }
                        }
                        if (selectionKey.isReadable() && !hasCompletedReceive(distributedIdChannel)) {
                            ByteBufferReceive networkReceive;
                            while ((networkReceive = distributedIdChannel.read()) != null) {
                                addToCompletedReceives(distributedIdChannel, networkReceive);
                            }
                        }
                        if (selectionKey.isWritable()) {
                            distributedIdChannel.write();
                        }
                        if (!selectionKey.isValid()) {
                            this.waitCloseConnections.offer(distributedIdChannel);
                        }
                    } catch (IOException exception) {
                        this.waitCloseConnections.offer(distributedIdChannel);
                    } catch (Exception e) {
                        log.error("未知异常", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("未知异常", e);
        }
    }

    /**
     * 一次只处理一个数据
     */
    private void processNewResponses() {
        //queue.peek，只取数据，不移除
        Response firstResponse = requestChannel.selectFirstResponse(processorId);
        try {
            if (firstResponse != null) {
                if (sendResponse(firstResponse)) {
                    //queue.poll，去数据，并移除
                    requestChannel.removeFirstResponse(processorId);
                }
            }
        } catch (Exception e) {
            //do nothing
            log.error("发送请求异常", e);
        }
    }

    /**
     * @return true：可以发送 false：不能发送
     */
    private boolean sendResponse(Response response) {
        DistributedIdChannel channel = channels.get(response.getDestination());
        return channel.setSend(response.getSend());
    }

    private void configureNewConnections() {
        while (!newConnections.isEmpty()) {
            SocketChannel channel = newConnections.poll();
            try {
                if (channel != null) {
                    SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_READ);
                    DistributedIdChannel distributedIdChannel = buildChannel(selectionKey, 1024);
                    this.channels.put(AddressUtil.getConnectionId(channel), distributedIdChannel);
                }
            } catch (Exception e) {
                log.error("创建连接失败", e);
            }
        }
    }

    private DistributedIdChannel buildChannel(SelectionKey key, @SuppressWarnings("SameParameterValue") int maxReceiveSize) throws DistributedIdException {
        DistributedIdChannel channel;
        try {
            IdGeneratorTransportLayer transportLayer = new IdGeneratorTransportLayer(key);
            channel = new DistributedIdChannel(transportLayer, maxReceiveSize);
            key.attach(channel);
        } catch (Exception e) {
            throw new DistributedIdException(e);
        }
        return channel;
    }

    /**
     * 关闭异常连接
     */
    private void handleDisconnected() {
        while (!waitCloseConnections.isEmpty()) {
            DistributedIdChannel distributedIdChannel = waitCloseConnections.poll();
            try {
                if (distributedIdChannel != null) {
                    log.warn("关闭异常连接:{}", distributedIdChannel.id());
                    distributedIdChannel.close();
                }
            } catch (Exception e) {
                log.error("关闭异常连接异常", e);
            }
        }
    }

    private void addToCompletedReceives(DistributedIdChannel channel, ByteBufferReceive receive) {
        if (!completedReceives.containsKey(channel)) {
            completedReceives.put(channel, new ArrayDeque<>());
        }
        Deque<ByteBufferReceive> deque = completedReceives.get(channel);
        deque.add(receive);
    }

    private boolean hasCompletedReceive(DistributedIdChannel channel) {
        return completedReceives.containsKey(channel);
    }

    private void handleCompletedReceives() {
        if (!this.completedReceives.isEmpty()) {
            Iterator<Map.Entry<DistributedIdChannel, Deque<ByteBufferReceive>>> iterator = this.completedReceives.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<DistributedIdChannel, Deque<ByteBufferReceive>> entry = iterator.next();
                DistributedIdChannel channel = entry.getKey();
                //selectionKey是否处于活跃状态，处于活跃状态才处理
                if (channel.isNotMute()) {
                    Deque<ByteBufferReceive> deque = entry.getValue();
                    ByteBufferReceive networkReceive;
                    while ((networkReceive = deque.poll()) != null) {
                        DistributedIdChannel distributedIdChannel = channels.get(networkReceive.source());
                        Request request = new Request(selector, networkReceive.payload(), distributedIdChannel.id(), processorId);
                        requestChannel.addRequest(request);
                    }
                    if (deque.isEmpty()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

}
