package com.geega.bsc.id.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * IdProperties
 *
 * @author Jun.An3
 * @date 2022/07/19
 */
@Data
@ConfigurationProperties(prefix = "id.cache")
public class CacheProperties {

    /**
     * eg:20
     */
    Integer capacity = 20;

    /**
     * eg:10
     */
    Integer trigger = 10;

}
