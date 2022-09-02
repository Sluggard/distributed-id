package com.geega.bsc.id.starter.configuration;

import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.client.cache.CacheConfig;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.starter.properties.CacheProperties;
import com.geega.bsc.id.starter.properties.ZkProperties;
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
@EnableConfigurationProperties(value = {ZkProperties.class, CacheProperties.class})
public class IdAutoConfiguration {

    @Bean
    public IdClient idClient(ZkProperties idProperties, CacheProperties cacheProperties) {
        ZkConfig zkConfig = ZkConfig.builder()
                .connection(idProperties.getConnection())
                .namespace(idProperties.getNamespace())
                .sessionTimeoutMs(idProperties.getSessionTimeoutMs())
                .connectionTimeoutMs(idProperties.getConnectionTimeoutMs())
                .build();

        CacheConfig cacheConfig = CacheConfig.builder()
                .trigger(cacheProperties.getTrigger())
                .capacity(cacheProperties.getCapacity())
                .build();
        return new IdClient(zkConfig, cacheConfig);
    }

}
