package com.geega.bsc.id.common;

import com.geega.bsc.id.common.factory.ZookeeperFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

/**
 * ZkTest
 *
 * @author Jun.An3
 * @date 2022/06/30
 */
@Slf4j
public class ZkTest1 {

    public static void main(String[] args) throws Exception {
        ZookeeperFactory zkFactory = new ZookeeperFactory();
        final CuratorFramework instance = zkFactory.instance();

        //true代表初始化时就获取节点的数据并且缓存到本地
        PathChildrenCache nodeCache = new PathChildrenCache(instance, "/distributed/id", true);
        nodeCache.start();

        //监听子节点的数据变化
        nodeCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        log.info("CHILD_ADDED:" + event.getData().getPath() + ",data:" + event.getData());
                        break;
                    case CHILD_REMOVED:
                        log.info("CHILD_REMOVED:" + event.getData().getPath() + ",data:" + event.getData());
                        break;
                    case CHILD_UPDATED:
                        log.info("CHILD_UPDATED:" + event.getData().getPath() + ",data:" + event.getData());
                        break;
                    case CONNECTION_LOST:
                        log.info("CONNECTION_LOST:" + event.getData().getPath() + ",data:" + event.getData());
                        break;
                    case CONNECTION_RECONNECTED:
                        log.info("CONNECTION_RECONNECTED:" + event.getData().getPath() + ",data:" + event.getData());
                        break;
                    case CONNECTION_SUSPENDED:
                        log.info("CONNECTION_SUSPENDED:" + event.getData().getPath() + ",data:" + event.getData());
                        break;
                    case INITIALIZED:
                        log.info("INITIALIZED:" + event.getData().getPath() + ",data:" + event.getData());
                        break;
                    default:
                        break;
                }
            }
        });

        //根据数据变化,去更新服务信息的缓存
        //当客户端与某个服务断开连接后,重新选取一个新的服务进行连接,并提供服务
        while (true) {
            Thread.sleep(5000);
        }
    }

}
