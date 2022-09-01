package com.geega.bsc.id.service.netty.parser;

import java.util.Properties;

/**
 * @author Jun.An3
 * @date 2022/09/01
 */
public class ServerConfig {

    private static final Properties PROPERTIES;

    static {
        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(ServerConfig.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (Exception e) {
            throw new RuntimeException("解析配置文件出错", e);
        }
    }

    public Integer getIdCacheCapacity() {
        return Integer.valueOf(PROPERTIES.getProperty("id.cache.capacity", "100"));
    }

    public Integer getIdCacheTrigger() {
        return Integer.valueOf(PROPERTIES.getProperty("id.cache.trigger", "50"));
    }

    public String getIdZkNameSpace() {
        return PROPERTIES.getProperty("id.zk.namespace", "id");
    }

    public String getIdZkConnection() {
        return PROPERTIES.getProperty("id.zk.connection", "127.0.0.1:2181");
    }

    public Integer getIdZkSessionTimeoutMs() {
        return Integer.valueOf(PROPERTIES.getProperty("id.zk.sessionTimeoutMs", "10000"));
    }

    public Integer getIdZkConnectionTimeoutMs() {
        return Integer.valueOf(PROPERTIES.getProperty("id.zk.connectionTimeoutMs", "10000"));
    }

    public Integer getServerPort() {
        return Integer.valueOf(PROPERTIES.getProperty("server.port", "8080"));
    }

}
