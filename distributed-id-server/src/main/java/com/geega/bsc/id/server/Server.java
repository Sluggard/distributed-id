package com.geega.bsc.id.server;

import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.common.sync.Sync;
import com.geega.bsc.id.common.utils.ResourcesUtil;
import com.geega.bsc.id.common.utils.SnowFlake;
import com.geega.bsc.id.server.config.ConfigConst;
import com.geega.bsc.id.server.config.ServerConfig;
import com.geega.bsc.id.server.network.ServerAcceptor;
import com.geega.bsc.id.server.network.ServerProcessor;
import com.geega.bsc.id.server.network.ServerRequestChannel;
import com.geega.bsc.id.server.network.ServerRequestHandler;
import com.geega.bsc.id.server.zk.ZkServer;
import lombok.extern.slf4j.Slf4j;
import java.util.Properties;

@Slf4j
public class Server {

    public static void main(String[] args) throws Exception {

        //同步组件
        Sync sync = new Sync();

        final Properties properties = ResourcesUtil.getProperties("/application.properties");

        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setIp(properties.getProperty(ConfigConst.BIND_IP));
        serverConfig.setPort(Integer.valueOf(properties.getProperty(ConfigConst.BIND_PORT)));

        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setNamespace(properties.getProperty(ConfigConst.ZK_NAMESPACE));
        zkConfig.setConnection(properties.getProperty(ConfigConst.ZK_CONNECTION));
        zkConfig.setSessionTimeoutMs(Integer.valueOf(properties.getProperty(ConfigConst.ZK_SESSION_TIMEOUT_MS)));
        zkConfig.setConnectionTimeoutMs(Integer.valueOf(properties.getProperty(ConfigConst.ZK_CONNECTION_TIMEOUT_MS)));

        //向zk注册，并定时心跳
        ZkServer zkServer = new ZkServer(serverConfig, zkConfig);

        ServerRequestChannel requestChannel = new ServerRequestChannel();

        ServerProcessor[] processors = new ServerProcessor[3];
        for (int i = 0; i < 3; i++) {
            processors[i] = new ServerProcessor(i, requestChannel);
        }

        //建立连接器
        ServerAcceptor acceptor = new ServerAcceptor(processors, serverConfig);
        acceptor.start();

        //开始接受请求
        ServerRequestHandler requestHandler = new ServerRequestHandler(requestChannel, new SnowFlake(1, zkServer.getWorkId()));
        requestHandler.start();

        //hold住
        sync.waitShutdown();
    }

}
