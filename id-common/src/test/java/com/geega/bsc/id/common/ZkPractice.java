package com.geega.bsc.id.common;

import com.geega.bsc.id.common.factory.ZookeeperFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

@Slf4j
public class ZkPractice {

    public static void main(String[] args) throws Exception {
        long now = System.currentTimeMillis();
        ZookeeperFactory zkFactory = new ZookeeperFactory();
        CuratorFramework client = zkFactory.instance();
        String data;
        //创建永久节点
        data = client.create().creatingParentsIfNeeded().forPath("/persistent/" + now, "data".getBytes());
        log.info(data);
        //创建永久临时节点
        data = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/persistent/sequential", "data".getBytes());
        log.info(data);
        data = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/persistent/sequential", "data".getBytes());
        log.info(data);
        //创建临时节点
        data = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/ephemeral/" + now, "data".getBytes());
        log.info(data);
        //创建临时顺序节点
        data = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/ephemeral/sequential/" + now, "data".getBytes());
        log.info(data);

        //先创建一个永久节点
        data = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/persistent/" + now + "/ephemeral1", "data".getBytes());
        log.info(data);
        data = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/persistent/" + now + "/ephemeral2", "data".getBytes());
        log.info(data);
    }

}
