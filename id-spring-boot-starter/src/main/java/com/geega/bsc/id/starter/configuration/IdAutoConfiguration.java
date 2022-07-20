/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.geega.bsc.id.starter.configuration;

import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.starter.properties.IdProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * IdAutoConfiguration
 *
 * @author Jun.An3
 * @date 2022/07/19
 */
@Configuration
@EnableConfigurationProperties({IdProperties.class})
public class IdAutoConfiguration {

    @Bean
    public IdClient idClient(IdProperties idProperties) {
        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setConnection(idProperties.getConnection());
        zkConfig.setNamespace(idProperties.getNamespace());
        zkConfig.setConnectionTimeoutMs(idProperties.getConnectionTimeoutMs());
        zkConfig.setSessionTimeoutMs(idProperties.getSessionTimeoutMs());
        return new IdClient(zkConfig);
    }

}
