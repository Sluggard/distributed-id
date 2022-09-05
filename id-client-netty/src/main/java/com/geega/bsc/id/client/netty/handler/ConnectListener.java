package com.geega.bsc.id.client.netty.handler;

/**
 * 连接状态监听器
 *
 * @author Jun.An3l
 * @date 2021/11/22
 */
public interface ConnectListener {

    void statusChanged(boolean connected);

}
