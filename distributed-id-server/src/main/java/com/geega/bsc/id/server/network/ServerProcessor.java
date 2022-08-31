package com.geega.bsc.id.server.network;

import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.network.ByteBufferReceive;
import com.geega.bsc.id.common.network.DistributedIdChannel;
import com.geega.bsc.id.common.network.IdGeneratorTransportLayer;
import com.geega.bsc.id.common.network.Send;
import com.geega.bsc.id.common.utils.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    private final List<ByteBufferReceive> completedReceives;

    private final Map<DistributedIdChannel, Deque<ByteBufferReceive>> stagedReceives;

    private final List<Send> completedSends;

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
        this.completedReceives = new ArrayList<>();
        this.stagedReceives = new HashMap<>();
        this.completedSends = new ArrayList<>();
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
                //从队列中拉取数据出来,完成selector注册
                configureNewConnections();
                //处理数据,将数据发送出去,就是写
                processNewResponses();
                //处理事件(读，写)
                handleEvent();
                //将一个业务完成read时的数据放入completedReceives中
                stagedToCompletedReceives();
                //对于读取到的请求,进行处理,然后处理完后,可能会把结果放入newResponses中,等待被发送出去
                handleCompletedReceives();
                //处理发送数据成功的channel
                handleCompletedSends();
                //处理待关闭连接
                handleDisconnected();
            } catch (Exception e) {
                log.error("未知异常", e);
            }
        }
    }

    private void handleEvent() {
        try {
            selector.select();
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
                    if (selectionKey.isReadable() && !hasStagedReceive(distributedIdChannel)) {
                        ByteBufferReceive networkReceive;
                        while ((networkReceive = distributedIdChannel.read()) != null) {
                            addToStagedReceives(distributedIdChannel, networkReceive);
                        }
                    }
                    if (selectionKey.isWritable()) {
                        Send send = distributedIdChannel.write();
                        if (send != null) {
                            this.completedSends.add(send);
                        }
                    }
                    if (!selectionKey.isValid()) {
                        this.waitCloseConnections.offer(distributedIdChannel);
                    }
                } catch (IOException exception) {
                    this.waitCloseConnections.offer(distributedIdChannel);
                    log.error("IO异常，连接：{}", distributedIdChannel.socketDescription());
                } catch (Exception e) {
                    log.error("未知异常", e);
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
                    String connectionId = AddressUtil.getConnectionId(channel);
                    log.info("创建连接：[{}]", connectionId);
                    SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_READ);
                    DistributedIdChannel distributedIdChannel = buildChannel(connectionId, selectionKey, 1024);
                    this.channels.put(connectionId, distributedIdChannel);
                }
            } catch (Exception e) {
                log.error("创建连接失败", e);
            }
        }
    }

    private DistributedIdChannel buildChannel(String id, SelectionKey key, @SuppressWarnings("SameParameterValue") int maxReceiveSize) throws DistributedIdException {
        DistributedIdChannel channel;
        try {
            IdGeneratorTransportLayer transportLayer = new IdGeneratorTransportLayer(key);
            channel = new DistributedIdChannel(id, transportLayer, maxReceiveSize);
            key.attach(channel);
        } catch (Exception e) {
            throw new DistributedIdException(e);
        }
        return channel;
    }

    private void handleCompletedReceives() {
        if (!completedReceives.isEmpty()) {
            Iterator<ByteBufferReceive> iterator = completedReceives.iterator();
            while (iterator.hasNext()) {
                ByteBufferReceive completedReceive = iterator.next();
                iterator.remove();
                DistributedIdChannel distributedIdChannel = channels.get(completedReceive.source());
                Request request = new Request(selector, completedReceive.payload(), distributedIdChannel.id(), processorId);
                requestChannel.addRequest(request);
                //当接收完（读取）数据时，就移除read事件
                distributedIdChannel.removeReadEvent();
            }
        }
    }

    private void handleCompletedSends() {
        for (Send send : completedSends) {
            String destination = send.destination();
            DistributedIdChannel distributedIdChannel = channels.get(destination);
            //当处理完写事件后，就再关注read事件
            distributedIdChannel.interestReadEvent();
        }
    }

    /**
     * 关闭异常连接
     */
    private void handleDisconnected() {
        while (!waitCloseConnections.isEmpty()) {
            DistributedIdChannel distributedIdChannel = waitCloseConnections.poll();
            try {
                if (distributedIdChannel != null) {
                    log.warn("关闭异常连接:{}", distributedIdChannel.socketDescription());
                    distributedIdChannel.close();
                }
            } catch (Exception e) {
                log.error("关闭连接异常", e);
            }
        }
    }

    private void addToStagedReceives(DistributedIdChannel channel, ByteBufferReceive receive) {
        if (!stagedReceives.containsKey(channel)) {
            stagedReceives.put(channel, new ArrayDeque<>());
        }
        Deque<ByteBufferReceive> deque = stagedReceives.get(channel);
        deque.add(receive);
    }

    private boolean hasStagedReceive(DistributedIdChannel channel) {
        return stagedReceives.containsKey(channel);
    }

    private void stagedToCompletedReceives() {
        if (!this.stagedReceives.isEmpty()) {
            Iterator<Map.Entry<DistributedIdChannel, Deque<ByteBufferReceive>>> iter = this.stagedReceives.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<DistributedIdChannel, Deque<ByteBufferReceive>> entry = iter.next();
                DistributedIdChannel channel = entry.getKey();
                //selectionKey是否处于活跃状态，处于活跃状态才处理
                if (channel.isNotMute()) {
                    Deque<ByteBufferReceive> deque = entry.getValue();
                    ByteBufferReceive networkReceive = deque.poll();
                    this.completedReceives.add(networkReceive);
                    if (deque.isEmpty()) {
                        iter.remove();
                    }
                }
            }
        }
    }

}
