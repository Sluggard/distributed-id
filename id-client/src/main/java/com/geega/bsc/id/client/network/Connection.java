package com.geega.bsc.id.client.network;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.client.zk.ZkClient;
import com.geega.bsc.id.common.address.ServerNode;
import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.network.ByteBufferReceive;
import com.geega.bsc.id.common.network.DistributedIdChannel;
import com.geega.bsc.id.common.network.IdGeneratorTransportLayer;
import com.geega.bsc.id.common.utils.ByteBufferUtil;
import com.geega.bsc.id.common.utils.SleepUtil;
import com.geega.bsc.id.common.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jun.An3
 * @date 2022/07/25
 */
@Slf4j
public class Connection {

    private final ZkClient zkClient;

    private final IdClient generator;

    private DistributedIdChannel distributedIdChannel;

    private Selector selector;

    private volatile boolean isRunning = true;

    private final ExecutorService executorService;

    private SocketChannel channel;

    public Connection(ZkClient zkClient, IdClient generator, ServerNode nodeAddress) {
        this.zkClient = zkClient;
        this.generator = generator;
        this.init(nodeAddress.getIp(), nodeAddress.getPort());
        //noinspection AlibabaThreadPoolCreation
        this.executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "Sender-Schedule");
            thread.setDaemon(true);
            return thread;
        });
        this.executorService.execute(new Sender());
        //超时5s无法创建连接，直接视为失败
        waitTimeoutThrowException();
    }

    private void waitTimeoutThrowException() {
        long nowMs = TimeUtil.nowMs();
        int timeoutMs = 5000;
        while (TimeUtil.nowMs() - nowMs < timeoutMs) {
            boolean connected = channel.isConnected();
            if (connected) {
                return;
            }
            SleepUtil.waitMs(100);
        }
        throw new DistributedIdException("超时5s建立连接");
    }

    private void init(String ip, int port) {
        try {
            this.channel = SocketChannel.open();
            this.channel.configureBlocking(false);
            this.channel.socket().setTcpNoDelay(true);
            this.channel.socket().setKeepAlive(true);
            this.channel.socket().setSendBufferSize(1024);
            this.selector = Selector.open();
            boolean connected = channel.connect(new InetSocketAddress(ip, port));
            SelectionKey selectionKey;
            if (connected) {
                selectionKey = channel.register(this.selector, SelectionKey.OP_READ);
            } else {
                selectionKey = channel.register(this.selector, SelectionKey.OP_CONNECT);
            }
            this.distributedIdChannel = buildChannel(selectionKey, 10 * 1024);
        } catch (Exception e) {
            this.isRunning = false;
            throw new DistributedIdException("建立连接异常", e);
        } finally {
            if (!isRunning) {
                close();
            }
        }
    }

    class Sender implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                try {
                    //监听操作系统是否有事件，事件来时，操作系统回调函数，让你阻塞状态唤醒
                    int select = selector.select();
                    if (select > 0) {
                        Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                        while (keysIterator.hasNext()) {
                            SelectionKey key = keysIterator.next();
                            keysIterator.remove();
                            if (!key.isValid()) {
                                isRunning = false;
                            } else if (key.isConnectable()) {
                                //连接事件
                                log.info("连接事件");
                                if (channel.isConnectionPending()) {
                                    boolean finishConnect = false;
                                    try {
                                        finishConnect = channel.finishConnect();
                                    } catch (Exception e) {
                                        isRunning = false;
                                    }
                                    if (finishConnect) {
                                        //关闭建立事件
                                        distributedIdChannel.removeConnectionEvent();
                                        distributedIdChannel.interestReadEvent();
                                        //注册客户端到zk
                                        zkClient.register(channel);
                                        break;
                                    }
                                }
                            } else if (key.isReadable()) {
                                //读取事件
                                log.info("读事件");
                                ByteBufferReceive receive;
                                while ((receive = distributedIdChannel.read()) != null) {
                                    receivePacket(receive);
                                }
                            } else if (key.isWritable()) {
                                //写事件
                                log.info("写事件");
                                distributedIdChannel.write();
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("处理事件异常", e);
                }
            }
            close(distributedIdChannel);
        }

    }

    boolean isValid() {
        return this.isRunning;
    }

    void close() {
        try {
            close(distributedIdChannel);
        } catch (Exception ignored) {
            //do nothing
        } finally {
            log.warn("关闭连接：{}", distributedIdChannel.id());
        }
    }

    private void close(DistributedIdChannel channel) {
        try {
            channel.close();
            this.executorService.shutdown();
            this.selector.close();
        } catch (Exception e) {
            log.warn("关闭连接异常", e);
        }
    }

    public void poll(int num) {
        //放入请求数据，如果上一次请求还没发送，就不用再次发送
        distributedIdChannel.setSend(ByteBufferUtil.getSend(null, num));
        //不管怎样，唤醒selector
        selector.wakeup();
    }

    private void receivePacket(ByteBufferReceive byteBufferReceive) {
        if (byteBufferReceive != null && byteBufferReceive.payload() != null) {
            ByteBuffer payload = byteBufferReceive.payload();
            String idsJsonString = ByteBufferUtil.byteBufferToString(payload);
            if (idsJsonString != null && idsJsonString.length() > 0) {
                List<Long> ids = JSON.parseArray(idsJsonString, Long.class);
                generator.cache(ids);
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

}
