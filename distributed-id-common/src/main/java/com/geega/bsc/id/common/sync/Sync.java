package com.geega.bsc.id.common.sync;

import java.util.concurrent.CountDownLatch;

/**
 * 同步组件
 *
 * @author Jun.An3
 * @date 2022/07/18
 */
public class Sync {

    private final CountDownLatch sync;

    public Sync() {
        this.sync = new CountDownLatch(1);
    }

    public void waitShutdown() throws InterruptedException {
        sync.await();
    }

    public void shutdown() {
        sync.countDown();
    }

}
