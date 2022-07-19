package com.geega.bsc.id.server.zk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * todo 类描述
 *
 * @author Jun.An3
 * @date 2022/07/11
 */
public class HeartBeatProcessor {

    private final ScheduledExecutorService executorService;

    public HeartBeatProcessor() {
        //初始化单线程定时任务执行器
        this.executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "heartbeat-schedule");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void sendHeartBeat(Runnable task) {
        //延迟3s，定时3s，执行任务
        this.executorService.scheduleAtFixedRate(task, 3, 3, TimeUnit.SECONDS);
    }

}
