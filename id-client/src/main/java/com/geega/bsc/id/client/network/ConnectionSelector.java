package com.geega.bsc.id.client.network;

import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.client.zk.ZkClient;
import com.geega.bsc.id.common.address.ServerNode;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;

/**
 * 处理器分配
 *
 * @author Jun.An3
 * @date 2022/07/18
 */
@Slf4j
public class ConnectionSelector {

    private final ZkClient zkClient;

    private final IdClient generator;

    private volatile Connection currentConnection;

    public ConnectionSelector(ZkClient zkClient, IdClient generator) {
        this.zkClient = zkClient;
        this.generator = generator;
    }

    public Connection dispatch() {
        if (currentConnection != null && currentConnection.isValid()) {
            return currentConnection;
        }
        checkClose();
        innerDispatch();
        return currentConnection;
    }

    private void innerDispatch() {
        if (currentConnection == null || !currentConnection.isValid()) {
            synchronized (this) {
                if (currentConnection == null || !currentConnection.isValid()) {
                    List<ServerNode> nodes = zkClient.getNodes();
                    if (nodes.isEmpty()) {
                        currentConnection = null;
                        return;
                    }
                    //筛选出最少连接的服务节点
                    final Optional<ServerNode> first = nodes.stream().sorted().findFirst();
                    try {
                        currentConnection = new Connection(zkClient, generator, first.get());
                    } catch (Exception e) {
                        currentConnection = null;
                        log.warn("无法获取连接", e);
                    }
                }
            }
        }
    }

    private void checkClose() {
        synchronized (this) {
            if (this.currentConnection != null && !this.currentConnection.isValid()) {
                this.currentConnection.close();
                //help gc
                this.currentConnection = null;
            }
        }
    }


}
