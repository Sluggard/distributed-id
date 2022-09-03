/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.geega.bsc.id.server.netty.starter;

import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.utils.IpUtil;
import com.geega.bsc.id.common.utils.ResourcesUtil;
import com.geega.bsc.id.common.utils.SnowFlake;
import com.geega.bsc.id.server.netty.config.ConfigConst;
import com.geega.bsc.id.server.netty.config.ServerConfig;
import com.geega.bsc.id.server.netty.file.LocalFile;
import com.geega.bsc.id.server.netty.server.IdTcpServer;
import com.geega.bsc.id.server.netty.zk.ZkServer;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import java.util.Properties;

/**
 * IdServerStarter
 *
 * @author Jun.An3
 * @date 2022/09/02
 */
@Slf4j
public class IdServerStarter {

    public static void main(String[] args) {
        //读取配置文件
        ServerConfig serverConfig = getServerConfig();

        //向zk注册，并定时心跳
        ZkServer zkServer = new ZkServer(serverConfig);

        //启动服务
        IdTcpServer idTcpServer = new IdTcpServer(serverConfig.getPort(), new SnowFlake(serverConfig.getIdDataCenter(), zkServer.getWorkId()));
        idTcpServer.start();

    }


    private static ServerConfig getServerConfig() {
        try {
            ServerConfig serverConfig = new ServerConfig();

            Properties properties = ResourcesUtil.getProperties("/application.properties");
            String ip = properties.getProperty(ConfigConst.BIND_IP);
            if (ip == null || "".equals(ip)) {
                ip = IpUtil.getIp();
                log.info("本机ip:{}", ip);
            }
            assert !Strings.isNullOrEmpty(ip);

            String port = System.getProperty("bind.port");
            if (port == null || "".equals(port)) {
                port = properties.getProperty(ConfigConst.BIND_PORT);
            }
            assert !Strings.isNullOrEmpty(port);
            assert !Strings.isNullOrEmpty(properties.getProperty(ConfigConst.ID_WORK_ID_ROOT));
            assert !Strings.isNullOrEmpty(properties.getProperty(ConfigConst.ID_WORK_ID_FILE));

            final LocalFile localFile = LocalFile.builder()
                    .root(properties.getProperty(ConfigConst.ID_WORK_ID_ROOT))
                    .fileName(properties.getProperty(ConfigConst.ID_WORK_ID_FILE))
                    .build();
            serverConfig.setLocalFile(localFile);

            serverConfig.setIp(ip);
            serverConfig.setPort(Integer.valueOf(port));
            serverConfig.setProcessor(Integer.valueOf(properties.getProperty(ConfigConst.NIO_PROCESSOR, "3")));
            serverConfig.setIdDataCenter(Long.valueOf(properties.getProperty(ConfigConst.ID_DATA_CENTER, "1")));
            serverConfig.setZkConnection(properties.getProperty(ConfigConst.ZK_CONNECTION, "127.0.0.1:2181"));
            serverConfig.setZkNamespace(properties.getProperty(ConfigConst.ZK_NAMESPACE, "default"));
            serverConfig.setZkConnectionTimeoutMs(Integer.valueOf(properties.getProperty(ConfigConst.ZK_CONNECTION_TIMEOUT_MS, "10000")));
            serverConfig.setZkSessionTimeoutMs(Integer.valueOf(properties.getProperty(ConfigConst.ZK_SESSION_TIMEOUT_MS, "10000")));

            return serverConfig;
        } catch (Exception e) {
            throw new DistributedIdException("读取配置文件异常", e);
        }
    }

}
