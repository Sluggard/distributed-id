package com.geega.bsc.id.common.network;

/**
 * @author Jun.An3
 * @date 2022/07/18
 */
public class ConnectionIdUtil {

    public static String getConnectionId(String localHost, int localPort, String remoteHost, int remotePort) {
        return localHost + ":" + localPort + "-" + remoteHost + ":" + remotePort;
    }

}
