package com.geega.bsc.id.client.netty.common;

import com.geega.bsc.id.client.netty.client.IdClient;
import com.geega.bsc.id.client.netty.zk.ZkClient;
import com.geega.bsc.id.common.address.ServerNode;
import com.geega.bsc.id.common.exception.DistributedIdException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;

/**
 * 连接池
 *
 * @author Jun.An3
 * @date 2022/09/05
 */
@Slf4j
public class ConnectionPool {

    private final ZkClient zkClient;

    private volatile NetClient currentNetClient;

    private final IdClient idClient;

    public ConnectionPool(ZkClient zkClient, IdClient idClient) {
        this.zkClient = zkClient;
        this.idClient = idClient;
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
                    //关闭无效的连接
                    closeNetClient();
                    //获取新连接
                    try {
                        currentNetClient = new NetClient(serverNode, zkClient, idClient);
                    } catch (Exception e) {
                        throw new DistributedIdException("创建连接失败", e);
                    }
                }
            }
        }
        return currentNetClient;
    }

    private void closeNetClient() {
        if (currentNetClient != null && currentNetClient.isClosed()) {
            currentNetClient.close();
        }
    }

}
