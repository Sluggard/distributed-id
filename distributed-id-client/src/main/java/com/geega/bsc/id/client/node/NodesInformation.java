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

    public List<NodeAddress> getNodes() {
        return this.nodes;
    }

    /**
     * 移除服务节点
     */
    public synchronized void remove(String ip, Integer port) {
        nodes.remove(NodeAddress.builder()
                .ip(ip)
                .port(port)
                .lastUpdateTime(TimeUtil.now())
                .build());
    }

    /**
     * 添加服务节点
     */
    public synchronized void add(NodeAddress nodeAddress) {
        if (!nodes.contains(nodeAddress)) {
            nodes.add(nodeAddress);
        }
    }

}
