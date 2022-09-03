package com.geega.bsc.id.server.netty.zk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
public class ZkHeartBeat {

    private final ScheduledExecutorService executorService;

    public ZkHeartBeat() {
        //初始化单线程定时任务执行器
        //noinspection AlibabaThreadPoolCreation
        this.executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "zk-heartbeat-schedule");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void sendHeartBeat(Runnable task) {
        //延迟3s，定时3s，执行任务
        this.executorService.scheduleAtFixedRate(task, 3, 3, TimeUnit.SECONDS);
    }

}
