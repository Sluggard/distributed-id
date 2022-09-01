package com.geega.bsc.id.client.network;

import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.client.zk.ZkClient;
import com.geega.bsc.id.common.address.ServerNode;
import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.utils.AddressUtil;
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
public class IdProcessorDispatch {

    private final ZkClient zkClient;

    private final IdClient generator;

    private volatile IdProcessor currentProcessor;

    public IdProcessorDispatch(ZkClient zkClient, IdClient generator) {
        this.zkClient = zkClient;
        this.generator = generator;
    }

    public IdProcessor dispatch() {
        if (currentProcessor != null && currentProcessor.isValid()) {
            log.info("使用连接：[{}]", AddressUtil.getConnectionId(currentProcessor.getSocketChannel()));
            return currentProcessor;
        }
        checkClose();
        innerDispatch();
        return currentProcessor;
    }

    private void innerDispatch() {
        if (currentProcessor == null || !currentProcessor.isValid()) {
            synchronized (this) {
                if (currentProcessor == null || !currentProcessor.isValid()) {
                    List<ServerNode> nodes = zkClient.getNodes();
                    if (nodes.isEmpty()) {
                        throw new DistributedIdException("无可用服务");
                    }
                    //筛选出最少连接的服务节点
                    final Optional<ServerNode> first = nodes.stream().sorted().findFirst();
                    currentProcessor = new IdProcessor(zkClient, generator, first.get());
                }
            }
        }
    }

    private void checkClose() {
        synchronized (this) {
            if (this.currentProcessor != null && !this.currentProcessor.isValid()) {
                this.currentProcessor.close();
                //help gc
                this.currentProcessor = null;
            }
        }
    }


}
