package com.geega.bsc.id.server.netty.config;

import com.geega.bsc.id.server.netty.file.LocalFile;
import lombok.Data;

/**
 * @author Jun.An3
 * @date 2022/07/14
 */
@Data
public class ServerConfig {

    private String ip;

    private Integer port;

    private Integer processor;

    private Long idDataCenter;

    private String zkNamespace;

    private String zkConnection;

    private Integer zkSessionTimeoutMs;

    private Integer zkConnectionTimeoutMs;

    private LocalFile localFile;

}
