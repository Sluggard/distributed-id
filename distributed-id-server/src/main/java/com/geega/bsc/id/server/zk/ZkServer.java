package com.geega.bsc.id.server.zk;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.common.address.ServerNode;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.common.constant.ZkTreeConstant;
import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.factory.ZookeeperFactory;
import com.geega.bsc.id.common.utils.AddressUtil;
import com.geega.bsc.id.server.config.ServerConfig;
import com.geega.bsc.id.server.local.LocalFile;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
public class ZkServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServer.class);

    private Integer workId;

    private final ServerConfig serverConfig;

    private final CuratorFramework zkClient;

    public ZkServer(ServerConfig serverConfig) {
        assert serverConfig != null;
        this.zkClient = new ZookeeperFactory(getZkConfig(serverConfig)).instance();
        this.serverConfig = serverConfig;
        this.init();
    }

    private ZkConfig getZkConfig(ServerConfig serverConfig) {
        return ZkConfig.builder()
                .connection(serverConfig.getZkConnection())
                .namespace(serverConfig.getZkNamespace())
                .sessionTimeoutMs(serverConfig.getZkSessionTimeoutMs())
                .connectionTimeoutMs(serverConfig.getZkConnectionTimeoutMs())
                .build();
    }

    private void init() {
        //创建临时节点
        register();
        //获取workId
        generateWorkId();
        //定时上传心跳
        sendHeartbeat();
    }

    /**
     * 定时上传心跳
     */
    private void sendHeartbeat() {
        ZkHeartBeat register = new ZkHeartBeat();
        register.sendHeartBeat(() -> {
            try {
                zkClient.setData().forPath(ZkTreeConstant.SERVER_ROOT + ZkTreeConstant.PATH_SEPARATOR + AddressUtil.getAddress(serverConfig.getIp(), serverConfig.getPort()), getDataBytes(serverConfig.getIp(), serverConfig.getPort()));
            } catch (KeeperException.NoNodeException noNodeException) {
                //创建临时节点
                register();
            } catch (Exception e) {
                LOGGER.error("定时上传心跳失败", e);
            }
        });
    }

    /**
     * 创建workId
     */
    private void generateWorkId() {
        try {
            LocalFile localFile = serverConfig.getLocalFile();
            Integer cacheWorkId = localFile.readWorkId();
            if (cacheWorkId != null && cacheWorkId != -1) {
                workId = cacheWorkId;
                LOGGER.info("获取本地workId：[{}]", workId);
            } else {
                final String nodePath = zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(ZkTreeConstant.WORK_ID_ROOT + ZkTreeConstant.PATH_SEPARATOR + "workid-", getDataBytes(serverConfig.getIp(), serverConfig.getPort()));
                workId = parseWorkId(nodePath);
                localFile.saveWorkId(workId);
                LOGGER.info("创建Zk-workId：[{}]", workId);
            }
        } catch (Exception e) {
            throw new DistributedIdException("创建自增workId失败", e);
        }
    }

    /**
     * 向zk注册服务
     */
    private void register() {
        try {
            String nodePath = ZkTreeConstant.SERVER_ROOT + ZkTreeConstant.PATH_SEPARATOR + AddressUtil.getAddress(serverConfig.getIp(), serverConfig.getPort());
            byte[] data = getDataBytes(serverConfig.getIp(), serverConfig.getPort());
            if (zkClient.checkExists().creatingParentsIfNeeded().forPath(nodePath) == null) {
                nodePath = zkClient.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(nodePath, data);
                LOGGER.info("向zk注册服务：[{}]", nodePath);
            } else {
                zkClient.setData().forPath(nodePath, data);
                LOGGER.info("已注册服务，只设置数据:[{}]", nodePath);
            }
        } catch (Exception e) {
            throw new DistributedIdException("向zk注册服务失败", e);
        }
    }

    public Integer getWorkId() {
        return this.workId;
    }

    private Integer parseWorkId(String nodePath) {
        String sequentialId = nodePath.replaceAll(ZkTreeConstant.WORK_ID_ROOT + ZkTreeConstant.PATH_SEPARATOR + "workid-", "");
        return Integer.valueOf(sequentialId);
    }

    private byte[] getDataBytes(String ip, int port) {
        final ServerNode netAddress = ServerNode.builder().ip(ip).port(port).build();
        return JSON.toJSONString(netAddress).getBytes();
    }

}
