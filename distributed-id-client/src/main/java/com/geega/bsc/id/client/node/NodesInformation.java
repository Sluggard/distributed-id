package com.geega.bsc.id.client.node;

import com.geega.bsc.id.common.address.NodeAddress;
import com.geega.bsc.id.common.utils.TimeUtil;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
public class NodesInformation {

    private final CopyOnWriteArrayList<NodeAddress> nodes = new CopyOnWriteArrayList<>();

    private final Integer serverNetworkPartitionTimeout = 15000;

    public List<NodeAddress> getNodes() {
        return this.nodes;
    }

    /**
     * 移除服务节点
     */
    public synchronized void remove(NodeAddress nodeAddress) {
        if (TimeUtil.now() - nodeAddress.getLastUpdateTime() > serverNetworkPartitionTimeout) {
            nodes.remove(nodeAddress);
        }
    }

    /**
     * 添加服务节点
     */
    public synchronized void update(NodeAddress nodeAddress) {
        if (nodes.contains(nodeAddress)) {

        }
    }

}
