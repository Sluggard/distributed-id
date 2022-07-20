package com.geega.bsc.id.client;

import com.geega.bsc.id.client.network.IdProcessorDispatch;
import com.geega.bsc.id.common.config.ZkConfig;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ID生成器
 *
 * @author Jun.An3
 * @date 2022/07/18
 */
public class IdClient {

    private final int capacity = 20;

    @SuppressWarnings("FieldCanBeLocal")
    private final int halfCapacity = capacity >> 1;

    private final LinkedBlockingQueue<Long> idQueue = new LinkedBlockingQueue<>(capacity);

    private final ExecutorService executorService;

    private final IdProcessorDispatch processorDispatch;

    public IdClient(ZkConfig zkConfig) {
        this.processorDispatch = new IdProcessorDispatch(new ZkClient(zkConfig), this);
        this.executorService = Executors.newSingleThreadExecutor();
        this.executeOnce(capacity);
    }

    /**
     * 获取ID
     */
    public Long id() {
        try {
            return idQueue.poll();
        } finally {
            try {
                if (idQueue.size() <= halfCapacity) {
                    expand();
                }
            } catch (Exception ignored) {
                //do nothing
            }
        }
    }

    private void expand() {
        this.executorService.execute(() -> executeOnce(halfCapacity));
    }

    private void executeOnce(int num) {
        try {
            processorDispatch.chooseOne().poll(num);
            while (idQueue.size() == 0) {
                Thread.sleep(1000);
            }
        } catch (Exception ignored) {
            //do nothing
        }
    }


    /**
     * 缓存ID
     */
    public void cache(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                idQueue.offer(id);
            }
        }
    }

}
