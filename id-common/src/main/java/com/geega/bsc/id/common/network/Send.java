package com.geega.bsc.id.common.network;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;

/**
 * 发送数据接口类
 *
 * @author Jun.An3
 * @date 2022/09/15
 */
public interface Send {

    boolean completed();

    long writeTo(GatheringByteChannel channel) throws IOException;

}