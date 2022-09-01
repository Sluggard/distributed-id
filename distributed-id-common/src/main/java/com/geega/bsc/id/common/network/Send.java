package com.geega.bsc.id.common.network;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;


public interface Send {

    boolean completed();

    long writeTo(GatheringByteChannel channel) throws IOException;

}