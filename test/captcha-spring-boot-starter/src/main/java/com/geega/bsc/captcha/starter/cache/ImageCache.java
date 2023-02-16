/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.geega.bsc.captcha.starter.cache;

import com.geega.bsc.captch.common.vo.CaptchaVO;
import com.geega.bsc.captcha.starter.service.CaptchaService;
import com.geega.bsc.captcha.starter.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 提前生成好可用的base64数据:
 * 图片切割和base64转换,比较耗时,所以提前生成好,直接使用
 * 优化性能
 *
 * @author Jun.An3
 * @date 2021/12/06
 */
@Slf4j
public class ImageCache {

    private final int max;

    private final AtomicReference<List<CaptchaVO>> readListReference;

    private final AtomicReference<List<CaptchaVO>> writeListReference;

    private final CaptchaService captchaService;

    public ImageCache(CaptchaService captchaService, int max, int period) {
        this.max = max;
        this.captchaService = captchaService;
        this.readListReference = new AtomicReference<>(new ArrayList<>(max));
        this.writeListReference = new AtomicReference<>(new ArrayList<>(max));
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        final ExecutorService asyncExecutorService = Executors.newSingleThreadExecutor();
        asyncExecutorService.execute(() -> {
            try {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                //先同步生成一批数据
                executorService.execute(() -> produceOnce(countDownLatch));
                try {
                    //放行
                    countDownLatch.await();
                } catch (Exception ignored) {
                    //do nothing
                }
                log.info("初始化图片缓存完成");
                //每隔60s生产一批新的数据
                executorService.scheduleWithFixedDelay(
                        ImageCache.this::produce,
                        period,
                        period,
                        TimeUnit.SECONDS);
            } catch (Exception e) {
                //do nothing
            }
        });
    }

    public int getMax() {
        return this.max;
    }

    /**
     * readMap生产满
     */
    private void produceOnce(CountDownLatch countDownLatch) {
        int count = 0;
        while (true) {
            try {
                readListReference.get().add(captchaService.generate());
                if (++count == max) {
                    //生产满了,就放行
                    break;
                }
            } catch (Exception ignored) {
            }
        }
        countDownLatch.countDown();
    }

    /**
     * 每隔1分钟生产一批新的数据在writeList中,并替换readList
     */
    private void produce() {
        int count = 0;
        while (true) {
            try {
                writeListReference.get().add(captchaService.generate());
                if (++count == max) {
                    //生产满了,readList和writeList就替换
                    readListReference.set(writeListReference.get());
                    //清空老的readList,并作为writeList
                    writeListReference.set(new ArrayList<>(max));
                    break;
                }
            } catch (Exception ignored) {
                //do nothing
            }
        }
    }

    /**
     * 从缓存里面取
     */
    public CaptchaVO get() {
        try {
            int randomIndex = RandomUtils.getRandomInt(0, readListReference.get().size() - 1);
            return readListReference.get().get(randomIndex);
        } catch (Exception ignored) {
        }
        return null;
    }

    public void clearCache() {
        //do nothing
    }

}
