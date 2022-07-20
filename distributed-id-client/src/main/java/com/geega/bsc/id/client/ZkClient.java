package com.geega.bsc.id.client;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.client.node.NodesInformation;
import com.geega.bsc.id.common.config.ZkConfig;
import com.geega.bsc.id.common.constant.ZkTreeConstant;
import com.geega.bsc.id.common.address.NodeAddress;
import com.geega.bsc.id.common.factory.ZookeeperFactory;
import com.geega.bsc.id.common.utils.TimeUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
public class ZkClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkClient.class);

    private final NodesInformation nodesInformation;

    private final ZkConfig zkConfig;

    public ZkClient(ZkConfig zkConfig) {
        this.zkConfig = zkConfig;
        this.nodesInformation = new NodesInformation();
        this.start();
    }

    public List<NodeAddress> getNodes() {
        return this.nodesInformation.getNodes();
    }

    private void start() {
        try {
            //创建zk客户端
            final ZookeeperFactory factory = new ZookeeperFactory(this.zkConfig);
            final CuratorFramework zkClient = factory.instance();

            //获取当前已注册服务
            final List<String> zkNodePaths = zkClient.getChildren().forPath(ZkTreeConstant.ZK_SERVER_ROOT);
            LOGGER.info("当前已注册服务节点：{}", JSON.toJSONString(zkNodePaths));
            for (String zkNodePath : zkNodePaths) {
                addNode(zkNodePath);
            }

            //监听某个节点
            //true代表初始化时就获取节点的数据并且缓存到本地
            PathChildrenCache nodeCache = new PathChildrenCache(zkClient, ZkTreeConstant.ZK_SERVER_ROOT, true);
            nodeCache.start();

            //监听子节点的数据变化
            nodeCache.getListenable().addListener((curatorFramework, event) -> {
                String path = event.getData().getPath();
                byte[] bytes = event.getData().getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        LOGGER.info("新增服务节点：{}", event.getData());
                        addNode(path);
                        break;
                    case CHILD_REMOVED:
                        LOGGER.info("删除服务节点：{}", event.getData());
                        removeNode(path);
                        break;
                    default:
                        LOGGER.info("其他事件：{}", event.getData());
                        break;
                }
            });

            long start = TimeUtil.now();
            while (nodesInformation.isNotReady()) {
                if (TimeUtil.now() - start >= 5000) {
                    break;
                }
                //稍微等待一下服务节点的注册
                try {
                    //noinspection BusyWait
                    Thread.sleep(500);
                } catch (Exception ignored) {
                    //do nothing
                }
            }
        } catch (Exception e) {
            LOGGER.error("初始化zk失败", e);
            throw new RuntimeException("初始化zk失败");
        }
    }

    private void addNode(String zkNodePath) {
        nodesInformation.add(getNodeAddress(zkNodePath));
    }

    private void removeNode(String zkNodePath) {
        final String address = zkNodePath.replaceAll(ZkTreeConstant.ZK_SERVER_ROOT + ZkTreeConstant.ZK_PATH_SEPARATOR, "");
        final String[] splits = address.split(":");
        nodesInformation.remove(splits[0], Integer.valueOf(splits[1]));
    }

    private NodeAddress getNodeAddress(String zkNodePath) {
        final String address = zkNodePath.replaceAll(ZkTreeConstant.ZK_SERVER_ROOT + ZkTreeConstant.ZK_PATH_SEPARATOR, "");
        final String[] splits = address.split(":");
        return NodeAddress.builder()
                .ip(splits[0])
                .port(Integer.valueOf(splits[1]))
                .lastUpdateTime(TimeUtil.now())
                .build();
    }

}
