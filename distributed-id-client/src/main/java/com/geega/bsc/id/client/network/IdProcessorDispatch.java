package com.geega.bsc.id.client.network;

import com.geega.bsc.id.client.ZkClient;
import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.common.dto.NodeAddress;
import java.util.List;

/**
 * 处理器分配
 *
 * @author Jun.An3
 * @date 2022/07/18
 */
public class IdProcessorDispatch {

    private final ZkClient zkClient;

    private final IdClient generator;

    private volatile IdProcessor processor;

    public IdProcessorDispatch(ZkClient zkClient, IdClient generator) {
        this.zkClient = zkClient;
        this.generator = generator;
    }

    public synchronized IdProcessor chooseOne() {
        try {
            if (processor == null) {
                final List<NodeAddress> nodes = zkClient.getNodes();
                processor = new IdProcessor("1", generator, nodes.get(0));
            }
        } catch (Exception e) {
            //获取异常
        }
        return processor;
    }

}
