/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

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
@ConfigurationProperties(prefix = "id.zk")
public class IdProperties {

    /**
     * eg:geely
     */
    String namespace;

    /**
     * eg:127.0.0.1:2181
     */
    String connection;

    /**
     * eg:10000
     */
    Integer sessionTimeoutMs;

    /**
     * eg:10000
     */
    Integer connectionTimeoutMs;

}
