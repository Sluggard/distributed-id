package com.geega.bsc.id.server;

import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.common.sync.Sync;
import com.geega.bsc.id.common.utils.SnowFlake;
import com.geega.bsc.id.server.config.ServerConfig;
import com.geega.bsc.id.server.network.ServerAcceptor;
import com.geega.bsc.id.server.network.ServerProcessor;
import com.geega.bsc.id.server.network.ServerRequestChannel;
import com.geega.bsc.id.server.network.ServerRequestHandler;
import com.geega.bsc.id.server.zk.ZkServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {

    public static void main(String[] args) throws Exception {

        //同步组件
        Sync sync = new Sync();

        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setIp("192.168.0.106");
        serverConfig.setPort(9999);

        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setNamespace("id");
        zkConfig.setConnection("127.0.0.1:2181");
        zkConfig.setSessionTimeoutMs(10000);
        zkConfig.setConnectionTimeoutMs(10000);

        //向zk注册，并定时心跳
        ZkServer zkServer = new ZkServer(serverConfig, zkConfig);
        zkServer.init();

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
