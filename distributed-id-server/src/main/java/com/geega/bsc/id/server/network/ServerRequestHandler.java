package com.geega.bsc.id.server.network;

import com.geega.bsc.id.common.utils.ByteBufferUtil;
import com.geega.bsc.id.common.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jun.An3
 * @date 2022/07/19
 */
public class ServerRequestHandler extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRequestHandler.class);

    private final ServerRequestChannel requestChannel;

    private final AtomicInteger threadIndex = new AtomicInteger(0);

    private final SnowFlake snowFlake;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4,
            4,
            10000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            r -> new Thread(r, "request-handler-thread-" + threadIndex.incrementAndGet()),
            (r, executor) -> LOGGER.info("丢弃")
    );

    public ServerRequestHandler(ServerRequestChannel requestChannel, SnowFlake snowFlake) {
        this.requestChannel = requestChannel;
        this.snowFlake = snowFlake;
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Request request = requestChannel.getRequest(5000);
                if (request != null) {
                    processRequest(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequest(final Request request) {
        Runnable task = () -> {
            try {
                getNextId(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        executor.execute(task);
    }

    private void getNextId(Request request) {

        int needNum = ByteBufferUtil.byteToIntV2(request.getData());
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < needNum; i++) {
            ids.add(snowFlake.nextId());
        }

        Response response = new Response(
                request.getConnectionId(),
                request.getProcessorId(),
                ByteBufferUtil.getSendForServer(request.getConnectionId(), ids)
        );

        LOGGER.info("数量:{},生成ID:{}", needNum, ids);
        requestChannel.addResponse(request.getProcessorId(), response);
    }

}
