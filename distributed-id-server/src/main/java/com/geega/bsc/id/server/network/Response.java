package com.geega.bsc.id.server.network;

import com.geega.bsc.id.common.network.Send;

/**
 * @author Jun.An3
 * @date 2022/07/19
 */
public class Response {

    private final String destination;

    private final Send send;

    public Response(String destination, Send send) {
        this.destination = destination;
        this.send = send;
    }

    public String getDestination() {
        return this.destination;
    }

    public Send getSend() {
        return this.send;
    }

}
