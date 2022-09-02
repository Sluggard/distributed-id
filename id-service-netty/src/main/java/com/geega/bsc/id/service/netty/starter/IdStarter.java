package com.geega.bsc.id.service.netty.starter;

import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.client.cache.CacheConfig;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.service.netty.handler.IdListRouteHandler;
import com.geega.bsc.id.service.netty.handler.IdOneRouteHandler;
import com.geega.bsc.id.service.netty.parser.ServerConfig;
import com.geega.bsc.id.service.netty.server.AbstractRouteHandler;
import com.geega.bsc.id.service.netty.server.IdHttpServer;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务启动类
 *
 * @author Jun.An3
 * @date 2022/09/01
 */
public class IdStarter {

    public static void main(String[] args) {

        //获取配置文件
        ServerConfig serverConfig = new ServerConfig();
        CacheConfig cacheConfig = CacheConfig.builder()
                .capacity(serverConfig.getIdCacheCapacity())
                .trigger(serverConfig.getIdCacheTrigger())
                .build();

        ZkConfig zkConfig = ZkConfig.builder()
                .connection(serverConfig.getIdZkConnection())
                .namespace(serverConfig.getIdZkNameSpace())
                .sessionTimeoutMs(serverConfig.getIdZkSessionTimeoutMs())
                .connectionTimeoutMs(serverConfig.getIdZkConnectionTimeoutMs())
                .build();

        IdClient idClient = new IdClient(zkConfig, cacheConfig);

        //配置路由<->请求处理器
        Map<String, AbstractRouteHandler> routeHandlerMap = new HashMap<>(2);
        routeHandlerMap.put("/api/v1/id/one", new IdOneRouteHandler(idClient));
        routeHandlerMap.put("/api/v1/id/list", new IdListRouteHandler(idClient));

        //启动服务
        IdHttpServer idHttpServer = new IdHttpServer(serverConfig.getServerPort(), routeHandlerMap);
        idHttpServer.start();

    }

}
