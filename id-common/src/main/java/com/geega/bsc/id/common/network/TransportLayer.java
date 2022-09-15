package com.geega.bsc.id.common.network;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SocketChannel;

/**
 * 传输层接口类
 * {@link ScatteringByteChannel} 定义通道从缓冲区中读取数据接口
 * {@link GatheringByteChannel} 定义通道往缓冲区中写入数据接口
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
public interface TransportLayer extends ScatteringByteChannel, GatheringByteChannel {

    /**
     * 完成连接
     */
    boolean finishConnect() throws IOException;

    /**
     * 获取SocketChannel
     */
    SocketChannel socketChannel();

    /**
     * 获取连接 ip1:port-ip2:port
     */
    String getConnectionId();

    /**
     * 添加事件
     */
    void addInterestOps(int ops);

    /**
     * 取消事件
     */
    void removeInterestOps(int ops);

    /**
     * 是否处于静默状态
     */
    boolean isMute();

}
