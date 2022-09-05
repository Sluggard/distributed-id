package com.geega.bsc.id.client.netty.common;

import com.geega.bsc.id.client.netty.zk.ZkClient;
import com.geega.bsc.id.common.address.ServerNode;
import com.geega.bsc.id.common.exception.DistributedIdException;
import java.util.List;
import java.util.Optional;

/**
 * 连接池
 *
 * @author Jun.An3
 * @date 2022/09/05
 */
public class ConnectionPool {

    private final ZkClient zkClient;

    private volatile NetClient currentNetClient;

    public ConnectionPool(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public NetClient one() {
        if (currentNetClient == null || currentNetClient.isClosed()) {
            synchronized (this) {
                if (currentNetClient == null || currentNetClient.isClosed()) {
                    //这里选择一台服务器
                    List<ServerNode> nodes = zkClient.getNodes();
                    if (nodes.isEmpty()) {
                        throw new DistributedIdException("无可用服务");
                    }
                    //筛选出最少连接的服务节点
                    Optional<ServerNode> suitServerNode = nodes.stream().sorted().findFirst();
                    ServerNode serverNode = suitServerNode.get();
                    currentNetClient = new NetClient(serverNode.getIp(), serverNode.getPort());
                }
            }
        }
        return currentNetClient;
    }

}
