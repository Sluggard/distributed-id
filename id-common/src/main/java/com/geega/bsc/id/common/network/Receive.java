package com.geega.bsc.id.common.network;

import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;

/**
 * 接受数据接口类
 *
 * @author Jun.An3
 * @date 2022/09/15
 */
public interface Receive {

    String source();

    boolean complete();

    long readFrom(ScatteringByteChannel channel) throws IOException;

}