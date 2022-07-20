package com.geega.bsc.id.client;

import com.geega.bsc.id.client.network.IdProcessorDispatch;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.common.utils.TimeUtil;
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

    @SuppressWarnings("FieldCanBeLocal")
    private final Integer initWaitTimeoutMs = 5000;

    public IdClient(ZkConfig zkConfig) {
        this.processorDispatch = new IdProcessorDispatch(new ZkClient(zkConfig), this);
        //noinspection AlibabaThreadPoolCreation
        this.executorService = Executors.newSingleThreadExecutor();
        this.initCacheTimeout();
    }

    private void initCacheTimeout() {
        executeOnceSync(capacity);
        long now = TimeUtil.now();
        while (TimeUtil.now() - now <= initWaitTimeoutMs) {
            try {
                //noinspection BusyWait
                Thread.sleep(1000);
            } catch (Exception ignored){
            }
        }
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
                    expandOnceAsync(halfCapacity);
                }
            } catch (Exception ignored) {
                //do nothing
            }
        }
    }

    private void expandOnceAsync(@SuppressWarnings("SameParameterValue") int num) {
        this.executorService.execute(() -> executeOnceSync(num));
    }

    private void executeOnceSync(int num) {
        try {
            this.processorDispatch.chooseOne().poll(num);
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
