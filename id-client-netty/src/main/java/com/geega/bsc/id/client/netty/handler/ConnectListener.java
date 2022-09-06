package com.geega.bsc.id.client.netty.handler;

/**
 * 连接状态监听器
 *
 * @author Jun.An3l
 * @date 2021/11/22
 */
public interface ConnectListener {

    /**
     * 连接状态监听器
     *
     * @param connected 状态，ture：建立连接，false：关闭连接
     */
    void statusChanged(boolean connected);

}
