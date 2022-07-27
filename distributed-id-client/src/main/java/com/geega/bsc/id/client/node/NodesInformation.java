package com.geega.bsc.id.client.node;

import com.geega.bsc.id.common.address.NodeAddress;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
@Slf4j
public class NodesInformation {

    private final CopyOnWriteArrayList<NodeAddress> nodes = new CopyOnWriteArrayList<>();

    public List<NodeAddress> getNodes() {
        return this.nodes;
    }

    /**
     * 移除服务节点
     */
    public void remove(NodeAddress nodeAddress) {
        log.info("移除服务节点：[{}]", nodeAddress.getAddress());
        nodes.remove(nodeAddress);
    }

    /**
     * 添加服务节点
     */
    public void update(NodeAddress nodeAddress) {
        if (!nodes.contains(nodeAddress)) {
            log.info("新增服务节点：[{}]", nodeAddress.getAddress());
            nodes.add(nodeAddress);
        }
    }

}
