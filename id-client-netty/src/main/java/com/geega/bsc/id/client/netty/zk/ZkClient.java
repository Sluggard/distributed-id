package com.geega.bsc.id.client.netty.zk;

import com.geega.bsc.id.client.netty.node.ServerNodeInformation;
import com.geega.bsc.id.common.address.ServerNode;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.common.constant.ZkTreeConstant;
import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.factory.ZookeeperFactory;
import com.geega.bsc.id.common.utils.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.CreateMode;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
@Slf4j
public class ZkClient {

    private final ServerNodeInformation nodesInformation;

    private final CuratorFramework client;

    public ZkClient(ZkConfig zkConfig) {
        this.client = new ZookeeperFactory(zkConfig).instance();
        this.nodesInformation = new ServerNodeInformation();
        this.start();
    }

    public List<ServerNode> getNodes() {
        return this.nodesInformation.getNodes();
    }

    public void register(String connectionId) {
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(ZkTreeConstant.CLIENT_ROOT + ZkTreeConstant.PATH_SEPARATOR + connectionId);
        } catch (Exception e) {
            throw new DistributedIdException("客户端向ZK注册失败", e);
        }
    }

    public void register(SocketChannel channel) {
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(ZkTreeConstant.CLIENT_ROOT + ZkTreeConstant.PATH_SEPARATOR + AddressUtil.getConnectionId(channel));
        } catch (Exception e) {
            throw new DistributedIdException("客户端向ZK注册失败", e);
        }
    }

    private void start() {
        try {

            //获取当前已注册服务
            final List<String> zkNodePaths = client.getChildren().forPath(ZkTreeConstant.SERVER_ROOT);
            for (String zkNodePath : zkNodePaths) {
                addServerNode(zkNodePath);
            }

            //监听服务列表
            //true代表初始化时就获取节点的数据并且缓存到本地
            PathChildrenCache serverNodeCache = new PathChildrenCache(client, ZkTreeConstant.SERVER_ROOT, true);
            serverNodeCache.start();
            serverNodeCache.getListenable().addListener((curatorFramework, event) -> {
                String path = event.getData().getPath();
                //root/127.0.0.1:2222
                //noinspection unused
                byte[] data = event.getData().getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                        addServerNode(path);
                        break;
                    case CHILD_REMOVED:
                        removeServerNode(path);
                        break;
                    default:
                        break;
                }
            });

            //监听客户端列表
            PathChildrenCache clientNodeCache = new PathChildrenCache(client, ZkTreeConstant.CLIENT_ROOT, true);
            clientNodeCache.start();
            clientNodeCache.getListenable().addListener((curatorFramework, event) -> {
                //root/127.0.0.1:2222-192.168.0.123:9999
                String path = event.getData().getPath();
                switch (event.getType()) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                        addClientInfo(path);
                        break;
                    case CHILD_REMOVED:
                        removeClientInfo(path);
                        break;
                    default:
                        break;
                }
            });

        } catch (Exception e) {
            throw new DistributedIdException("初始化zk失败", e);
        }
    }

    private void removeClientInfo(String zkNodePath) {
        final String connectionId = zkNodePath.replaceAll(ZkTreeConstant.CLIENT_ROOT + ZkTreeConstant.PATH_SEPARATOR, "");
        final String[] addresses = connectionId.split("-");
        final String clientAddress = addresses[0];
        final String serverAddress = addresses[1];
        final String[] ipPort = serverAddress.split(":");
        this.nodesInformation.removeClientInfo(ipPort[0], Integer.valueOf(ipPort[1]), clientAddress);
    }

    private void addClientInfo(String zkNodePath) {
        final String connectionId = zkNodePath.replaceAll(ZkTreeConstant.CLIENT_ROOT + ZkTreeConstant.PATH_SEPARATOR, "");
        final String[] addresses = connectionId.split("-");
        final String clientAddress = addresses[0];
        final String serverAddress = addresses[1];
        final String[] ipPort = serverAddress.split(":");
        this.nodesInformation.addClientInfo(ipPort[0], Integer.valueOf(ipPort[1]), clientAddress);
    }


    private void addServerNode(String zkNodePath) {
        final String address = zkNodePath.replaceAll(ZkTreeConstant.SERVER_ROOT + ZkTreeConstant.PATH_SEPARATOR, "");
        final String[] addresses = address.split(":");
        this.nodesInformation.updateServerNode(addresses[0], Integer.valueOf(addresses[1]));
    }

    private void removeServerNode(String zkNodePath) {
        final String address = zkNodePath.replaceAll(ZkTreeConstant.SERVER_ROOT + ZkTreeConstant.PATH_SEPARATOR, "");
        final String[] addresses = address.split(":");
        this.nodesInformation.removeServerNode(addresses[0], Integer.valueOf(addresses[1]));
    }

}
