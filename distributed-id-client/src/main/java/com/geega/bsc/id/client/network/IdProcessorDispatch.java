package com.geega.bsc.id.client.network;

import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.client.ZkClient;
import com.geega.bsc.id.common.address.NodeAddress;
import com.geega.bsc.id.common.exception.DistributedIdException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理器分配
 *
 * @author Jun.An3
 * @date 2022/07/18
 */
public class IdProcessorDispatch {

    private final ZkClient zkClient;

    private final IdClient generator;

    private volatile IdProcessor currentProcessor;

    private final AtomicInteger id = new AtomicInteger(1);

    public IdProcessorDispatch(ZkClient zkClient, IdClient generator) {
        this.zkClient = zkClient;
        this.generator = generator;
    }

    public IdProcessor dispatch() {
        if (currentProcessor != null && currentProcessor.isValid()) {
            return currentProcessor;
        }
        checkClose();
        innerDispatch();
        return currentProcessor;
    }

    private synchronized void innerDispatch() {
        if (currentProcessor == null || !currentProcessor.isValid()) {
            synchronized (this) {
                if (currentProcessor == null || !currentProcessor.isValid()) {
                    List<NodeAddress> nodes = zkClient.getNodes();
                    if (nodes.isEmpty()) {
                        throw new DistributedIdException("无可用服务");
                    }
                    NodeAddress nodeAddress = null;
                    for (NodeAddress node : nodes) {
                        if (currentProcessor != null) {
                            if (!node.getAddress().equals(currentProcessor.getAddress())) {
                                nodeAddress = node;
                            }
                        } else {
                            nodeAddress = node;
                        }
                    }
                    if (nodeAddress == null) {
                        throw new DistributedIdException("无可用服务");
                    }
                    currentProcessor = new IdProcessor(String.valueOf(id.getAndIncrement()), generator, nodeAddress);
                }
            }
        }
    }

    private synchronized void checkClose() {
        if (this.currentProcessor != null && !this.currentProcessor.isValid()) {
            this.currentProcessor.close();
        }
    }


}
