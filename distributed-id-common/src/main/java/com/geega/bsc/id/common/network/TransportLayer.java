package com.geega.bsc.id.common.network;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Jun.An3
 * @date 2022/08/29
 */
public interface TransportLayer extends ScatteringByteChannel, GatheringByteChannel {

    boolean finishConnect() throws IOException;

    SocketChannel socketChannel();

    boolean hasPendingWrites();

    void addInterestOps(int ops);

    void removeInterestOps(int ops);

    boolean isMute();

}
