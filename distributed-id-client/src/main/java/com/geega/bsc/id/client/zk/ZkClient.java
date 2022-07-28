package com.geega.bsc.id.client.zk;

import com.geega.bsc.id.client.node.NodesInformation;
import com.geega.bsc.id.common.address.NodeAddress;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.common.constant.ZkTreeConstant;
import com.geega.bsc.id.common.exception.DistributedIdException;
import com.geega.bsc.id.common.factory.ZookeeperFactory;
import com.geega.bsc.id.common.utils.AddressUtil;
import com.geega.bsc.id.common.utils.TimeUtil;
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

    private final NodesInformation nodesInformation;

    private final CuratorFramework client;

    public ZkClient(ZkConfig zkConfig) {
        this.client = new ZookeeperFactory(zkConfig).instance();
        this.nodesInformation = new NodesInformation();
        this.start();
    }

    public List<NodeAddress> getNodes() {
        return this.nodesInformation.getNodes();
    }

    public void register(SocketChannel channel) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ZkTreeConstant.CLIENT_ROOT + ZkTreeConstant.PATH_SEPARATOR + AddressUtil.getConnectionId(channel));
        } catch (Exception e) {
            throw new DistributedIdException("客户端向ZK注册失败", e);
        }
    }

    private void start() {
        try {

            //获取当前已注册服务
            final List<String> zkNodePaths = client.getChildren().forPath(ZkTreeConstant.SERVER_ROOT);
            for (String zkNodePath : zkNodePaths) {
                updateNode(zkNodePath);
            }

            //监听服务列表
            //true代表初始化时就获取节点的数据并且缓存到本地
            PathChildrenCache serverNodeCache = new PathChildrenCache(client, ZkTreeConstant.SERVER_ROOT, true);
            serverNodeCache.start();
            serverNodeCache.getListenable().addListener((curatorFramework, event) -> {
                String path = event.getData().getPath();
                //noinspection unused
                byte[] data = event.getData().getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                        updateNode(path);
                        break;
                    case CHILD_REMOVED:
                        removeNode(path);
                        break;
                    default:
                        break;
                }
            });

            //监听客户端列表
            PathChildrenCache clientNodeCache = new PathChildrenCache(client, ZkTreeConstant.CLIENT_ROOT, true);
            clientNodeCache.start();
            clientNodeCache.getListenable().addListener((curatorFramework, event) -> {
                String path = event.getData().getPath();
                switch (event.getType()) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                        break;
                    case CHILD_REMOVED:
                        break;
                    default:
                        break;
                }
            });

        } catch (Exception e) {
            throw new DistributedIdException("初始化zk失败", e);
        }
    }

    private void updateNode(String zkNodePath) {
        this.nodesInformation.update(getNodeAddress(zkNodePath));
    }

    private void removeNode(String zkNodePath) {
        this.nodesInformation.remove(getNodeAddress(zkNodePath));
    }

    private NodeAddress getNodeAddressClientAlive(String zkNodePath) {
        String[] addresses = zkNodePath.split("-");
        String clientAddress = addresses[0];
        String serverAddress = addresses[1];
        final String[] splits = serverAddress.split(":");
        return NodeAddress.builder().ip(splits[0]).port(Integer.valueOf(splits[1])).lastUpdateTime(TimeUtil.now()).build();
    }

    private NodeAddress getNodeAddress(String zkNodePath) {
        final String address = zkNodePath.replaceAll(ZkTreeConstant.SERVER_ROOT + ZkTreeConstant.PATH_SEPARATOR, "");
        final String[] splits = address.split(":");
        return NodeAddress.builder().ip(splits[0]).port(Integer.valueOf(splits[1])).lastUpdateTime(TimeUtil.now()).build();
    }

}
