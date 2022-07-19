package com.geega.bsc.id.server.network;

import java.nio.ByteBuffer;

/**
 * @author Jun.An3
 * @date 2022/07/18
 */
public class Request {

    private final int processorId;

    private final String connectionId;

    private final ByteBuffer data;

    public Request(ByteBuffer data, String connectionId, int processorId) {
        this.data = data;
        this.connectionId = connectionId;
        this.processorId = processorId;
    }

    public String getConnectionId() {
        return this.connectionId;
    }

    public int getProcessorId() {
        return this.processorId;
    }

    public ByteBuffer getData() {
        return this.data;
    }

}
