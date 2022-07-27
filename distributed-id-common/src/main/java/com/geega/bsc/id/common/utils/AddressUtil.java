package com.geega.bsc.id.common.utils;

import java.nio.channels.SocketChannel;

/**
 * @author Jun.An3
 * @date 2022/07/25
 */
public class AddressUtil {

    public static String getAddress(String ip, Integer port) {
        return ip + ":" + port;
    }

    public static String getConnectionId(SocketChannel channel) {
        if (channel != null) {
            String localHost = channel.socket().getLocalAddress().getHostAddress();
            int localPort = channel.socket().getLocalPort();
            String remoteHost = channel.socket().getInetAddress().getHostAddress();
            int remotePort = channel.socket().getPort();
            return getConnectionId(localHost, localPort, remoteHost, remotePort);
        }
        return null;
    }

    private static String getConnectionId(String localHost, int localPort, String remoteHost, int remotePort) {
        return localHost + ":" + localPort + "-" + remoteHost + ":" + remotePort;
    }

}
