package com.geega.bsc.id.service.netty.starter;

import com.geega.bsc.id.client.IdClient;
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
        ServerConfig serverConfig = new ServerConfig();
        IdClient idClient = new IdClient();


        Map<String, AbstractRouteHandler> routeHandlerMap = new HashMap<>();
        routeHandlerMap.put("/api/v1/id/one", );
        new IdHttpServer(9000, );
    }

}
