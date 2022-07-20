package com.geega.bsc.id.server.zk;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.common.address.NodeAddress;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.common.constant.ZkTreeConstant;
import com.geega.bsc.id.common.factory.ZookeeperFactory;
import com.geega.bsc.id.common.utils.TimeUtil;
import com.geega.bsc.id.server.config.ServerConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
public class ZkServer {

    private final Logger LOGGER = LoggerFactory.getLogger(ZkServer.class);

    private volatile Integer workId;

    private final ServerConfig serverConfig;

    private final CuratorFramework zkClient;

    public ZkServer(ServerConfig serverConfig, ZkConfig zkConfig) {
        this.serverConfig = serverConfig;
        this.zkClient = new ZookeeperFactory(zkConfig).instance();
        this.init();
    }

    private void init() {
        //创建临时节点
        addEphemeralSequential();
        //创建临时节点（自增id）
        addEphemeralSequentialWorkId();
        //定时上传心跳
        sendHeartbeat();
    }

    /**
     * 定时上传心跳
     */
    private void sendHeartbeat() {
        HeartBeatProcessor register = new HeartBeatProcessor();
        register.sendHeartBeat(() -> {
            try {
                Stat stat = zkClient.setData().forPath(ZkTreeConstant.ZK_SERVER_ROOT + ZkTreeConstant.ZK_PATH_SEPARATOR + getAddress(serverConfig.getIp(), serverConfig.getPort()), getDataBytes(serverConfig.getIp(), serverConfig.getPort()));
                LOGGER.info("定时上报心跳：{}", stat);
            } catch (KeeperException.NoNodeException noNodeException) {
                //创建临时节点
                addEphemeralSequential();
            } catch (Exception e) {
                LOGGER.error("定时上传心跳失败", e);
            }
        });
    }

    /**
     * 创建临时节点（自增id）
     */
    private void addEphemeralSequentialWorkId() {
        try {
            final String nodePath = zkClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ZkTreeConstant.ZK_WORK_ID_ROOT + ZkTreeConstant.ZK_PATH_SEPARATOR + "workid-", getDataBytes(serverConfig.getIp(), serverConfig.getPort()));
            workId = generateWorkId(nodePath);
            LOGGER.info("创建的临时顺序节点：{}", nodePath);
        } catch (Exception e) {
            LOGGER.error("创建临时节点失败", e);
            throw new RuntimeException("创建的临时顺序节点");
        }
    }

    /**
     * 创建临时节点
     */
    private void addEphemeralSequential() {
        try {
            final String nodePath = zkClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(ZkTreeConstant.ZK_SERVER_ROOT + ZkTreeConstant.ZK_PATH_SEPARATOR + getAddress(serverConfig.getIp(), serverConfig.getPort()), getDataBytes(serverConfig.getIp(), serverConfig.getPort()));
            LOGGER.info("创建的临时节点：{}", nodePath);
        } catch (Exception e) {
            LOGGER.error("创建临时节点失败", e);
            throw new RuntimeException("创建临时节点失败");
        }
    }

    public Integer getWorkId() {
        return this.workId;
    }

    private Integer generateWorkId(String nodePath) {
        String sequentialId = nodePath.replaceAll(ZkTreeConstant.ZK_WORK_ID_ROOT + ZkTreeConstant.ZK_PATH_SEPARATOR + "workid-", "");
        return Integer.valueOf(sequentialId);
    }

    private String getAddress(String ip, int port) {
        return ip + ":" + port;
    }

    private byte[] getDataBytes(String ip, int port) {
        final NodeAddress netAddress = NodeAddress.builder()
                .ip(ip)
                .port(port)
                .lastUpdateTime(TimeUtil.now())
                .build();
        return JSON.toJSONString(netAddress).getBytes();
    }

}
