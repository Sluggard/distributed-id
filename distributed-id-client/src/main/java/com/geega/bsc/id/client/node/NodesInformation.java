package com.geega.bsc.id.client.node;

import com.geega.bsc.id.common.address.NodeAddress;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
@Slf4j
public class NodesInformation {

    private final ConcurrentHashMap<String,NodeAddress> nodeMap = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<NodeAddress> nodes = new CopyOnWriteArrayList<>();

    public List<NodeAddress> getNodes() {
        return this.nodes;
    }

    public synchronized void updateClientAlive(NodeAddress nodeAddress, String client) {
        if (nodes.contains(nodeAddress)){

        }
    }

    /**
     * 移除服务节点
     */
    public void remove(NodeAddress nodeAddress) {
        nodes.remove(nodeAddress);
        log.info("移除服务节点：[{}]", nodeAddress.getAddress());
    }

    /**
     * 添加服务节点
     */
    public void update(NodeAddress nodeAddress) {
        if (!nodes.contains(nodeAddress)) {
            nodes.add(nodeAddress);
            log.info("新增服务节点：[{}]", nodeAddress);
        } else {
            for (NodeAddress node : nodes) {
                if (node.equals(nodeAddress)) {
                    node.setLastUpdateTime(nodeAddress.getLastUpdateTime());
                    node.setClientAlive(nodeAddress.getClientAlive());
                    log.info("更新服务节点：[{}]", node);
                    break;
                }
            }
        }
    }

}
