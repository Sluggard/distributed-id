/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.geega.bsc.id.client.netty.test;

import com.geega.bsc.id.client.netty.client.IdClient;
import com.geega.bsc.id.client.netty.config.CacheConfig;
import com.geega.bsc.id.common.config.ZkConfig;

/**
 * Test
 *
 * @author Jun.An3
 * @date 2022/09/05
 */
public class Test {

    public static void main(String[] args) {

        IdClient idClient = new IdClient(getZkConfig(), getCacheConfig());
        for (int i = 0; i < 5; i++) {
            System.out.println("id：" + idClient.id());
        }
    }

    private static ZkConfig getZkConfig() {
        return ZkConfig.builder()
                .connection("127.0.0.1:2181")
                .namespace("id")
                .sessionTimeoutMs(10000)
                .connectionTimeoutMs(10000)
                .build();

    }

    private static CacheConfig getCacheConfig() {
        return CacheConfig.builder()
                .capacity(100)
                .trigger(60)
                .build();
    }

}
