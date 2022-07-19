/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.geega.bsc.id.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * IdProperties
 *
 * @author Jun.An3
 * @date 2022/07/19
 */
@ConfigurationProperties(prefix = "geely.id")
public class IdProperties {

    private String zkAddress = "127.0.0.1:2181";

    private String zkNamespace = "geely";

}
