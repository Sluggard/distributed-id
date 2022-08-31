package com.geega.bsc.id.common.network;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SocketChannel;

/**
 * {@link ScatteringByteChannel} 定义通道从缓冲区中读取数据接口
 * {@link GatheringByteChannel} 定义通道往缓冲区中写入数据接口
 *
 * @author Jun.An3
 * @date 2022/08/29
 */
public interface TransportLayer extends ScatteringByteChannel, GatheringByteChannel {

    boolean finishConnect() throws IOException;

    SocketChannel socketChannel();

    void addInterestOps(int ops);

    void removeInterestOps(int ops);

    boolean isMute();

}
