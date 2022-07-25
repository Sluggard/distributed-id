package com.geega.bsc.id.common.utils;

/**
 * @author Jun.An3
 * @date 2022/07/25
 */
public class AddressUtil {

    public static String getAddress(String ip, Integer port) {
        return ip + ":" + port;
    }

    public static String getConnectionId(String localHost, int localPort, String remoteHost, int remotePort) {
        return localHost + ":" + localPort + "-" + remoteHost + ":" + remotePort;
    }

}
