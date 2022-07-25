package com.geega.bsc.id.client;

import com.geega.bsc.id.client.network.IdProcessorDispatch;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.common.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ID生成器
 *
 * @author Jun.An3
 * @date 2022/07/18
 */
public class IdClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdClient.class);

    private final int capacity = 20;

    @SuppressWarnings("FieldCanBeLocal")
    private final int halfCapacity = capacity >> 1;

    private final LinkedBlockingQueue<Long> idQueue = new LinkedBlockingQueue<>(capacity);

    private final ExecutorService executorService;

    private final IdProcessorDispatch processorDispatch;

    @SuppressWarnings("FieldCanBeLocal")
    private final Integer initWaitTimeoutMs = 5000;

    private final AtomicBoolean isExpanding = new AtomicBoolean(false);

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
                if (idQueue.size() > 0) {
                    break;
                }
            } catch (Exception ignored) {
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
                if (idQueue.size() <= halfCapacity && !isExpanding.get()) {
                    isExpanding.compareAndSet(false, true);
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
            this.processorDispatch.dispatch().poll(num);
        } catch (Exception ignored) {
            //do nothing
        }
    }

    /**
     * 缓存ID
     */
    public void cache(List<Long> ids) {
        LOGGER.info("前：当前id缓存数：{}", idQueue.size());
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                idQueue.offer(id);
            }
        }
        LOGGER.info("后：当前id缓存数：{}", idQueue.size());
        isExpanding.compareAndSet(true, false);
    }

}
