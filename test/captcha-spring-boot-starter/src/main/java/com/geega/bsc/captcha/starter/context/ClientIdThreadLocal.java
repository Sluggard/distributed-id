package com.geega.bsc.captcha.starter.context;

import lombok.extern.slf4j.Slf4j;

/**
 * client id 线程共享组件
 *
 * @author Jun.An3
 * @date 2021/11/29
 */
@Slf4j
public class ClientIdThreadLocal {

    public static final ThreadLocal<String> clientId = new ThreadLocal<>();

    public static void set(String clientId) {
        log.info("clientId:" + clientId);
        ClientIdThreadLocal.clientId.set(clientId);
    }

    public static void reset() {
        clientId.remove();
    }

    public static String get() {
        return clientId.get();
    }

}
