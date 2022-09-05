package com.geega.bsc.id.client.netty.network;

import com.geega.bsc.id.client.netty.client.IdClient;
import com.geega.bsc.id.client.netty.zk.ZkClient;
import com.geega.bsc.id.common.address.ServerNode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jun.An3
 * @date 2022/07/25
 */
@Slf4j
public class IdProcessor {

    public IdProcessor(ZkClient zkClient, IdClient idClient, ServerNode serverNode) {

    }

    public boolean isValid() {
        return false;
    }

    public String getConnectionId() {
        return null;
    }

    public void close() {

    }

    public void poll(int num) {

    }

}
