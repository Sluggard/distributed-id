##### 模块介绍

- distributed-id-common

```
公共包
```

- distributed-id-client

```
客户端，包含distributed-id-common模块
```

- distributed-id-server

```
服务端，包含distributed-id-common模块
```

- id-spring-boot-starter

```
Spring-boot-starter模块，包含distributed-id-client、distributed-id-common模块
```

- id-server

```
基于Spring-boot-starter模块，提供HTTP方式获取ID的应用，包含distributed-id-common、distributed-id-client、id-spring-boot-starter模块
```

##### 如何集成

- install id-spring-boot-starter模块到本地Maven仓库
- pom中引入如下依赖

```
<dependency>
    <groupId>com.geega.cloud</groupId>
    <artifactId>id-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

- 使用IdClient客户端

```
@Autowired
private IdClient idClient;
```

##### 服务端参数详解

```
# 服务IP
bind.ip=127.0.0.1
# 服务端口
bind.port=9999
# 存放机器id文件父目录
id.workid.root=F:\\tmp
# 存放几区id文件名
id.workid.file=workid.txt
# SnowFlake算法中的数据中心号
id.datacenter=1
# 基于NIO实现的Reactor模式时，Processor数量
nio.processor=3
# zk的连接，集群时，例子：127.0.0.1:2181，127.0.0.1:2182
zk.connection=127.0.0.1:2181
# zk命名空间
zk.namespace=id
# zk会话超时
zk.sessionTimeoutMs=10000
# zk连接超时
zk.connectionTimeoutMs=10000
```

##### 客户端参数详解

```
# ID缓存容量个数
id.cache.capacity=40
# ID缓存少于triggerExpand时，会触发拉取操作，拉取数量为capacity - triggerExpand
id.cache.triggerExpand=20
# zk命名空间
id.zk.namespace=id
# zk的连接，集群时，例子：127.0.0.1:2181，127.0.0.1:2182
id.zk.connection=127.0.0.1:2181
# zk会话超时
id.zk.sessionTimeoutMs=10000
# zk连接超时
id.zk.connectionTimeoutMs=10000
```

##### 待办事项

- ~~当客户端主动关闭连接时，服务端检测主动关闭该连接~~
- ~~第一次启动客户端时，先获取一批缓存，超时后，还未获取到数据，直接抛异常~~
- ~~新增一个spring-boot-starter，集成方便~~
- ~~服务端的可配置项，通过application.properties配置~~
- ~~客户端连接不上zk时，抛出异常，服务无法启动~~
- ~~增加spring-boot-starter测试模块~~
- ~~优化代码结构，结构更清晰~~
- ~~客户端拿到服务端的地址列表时，选择服务，并创建连接，创建连接后，以后每次调用都使用该连接，直到该连接断开~~
- ~~编写使用文档，参数详解~~
- ~~如何解决频繁启动后，zk自增的id达到snowflake中machineId的最大值，如何处理~~
- ~~优化客户端连接释放时，资源的释放~~
- ~~实现id server服务，提供http接口方式获取ID~~
- ~~优化返回给前端id数据不能为null~~
- ~~优化无需强依赖服务端启动~~
- ~~当客户端与服务端出现网络分区时的应对措施，重试？抛异常？~~
- ~~当获取不到ID时，想一下如何处理，出现获取不到ID时，要么出现短暂网络分区，要么是服务挂掉，服务挂掉不再考虑，考虑下短暂网络分区，使用超时返回~~
- ~~日志优化~~
- ~~优化：客户端可以选择【最少客户端连接】的服务端，/client/ip1:port1-ip2:port2，然后客户端监听/client目录~~
- ~~压测出现问题：DistributedIdChannel.setSend(Send) throw new DistributedIdException("异常：上一个Send请求未完成，又开始Send请求了");~~
- ~~发现当前连接已断开，移除客户端中服务节点缓存~~
- 根据不同网络，硬件资源，输出QPS压测数据表格
- 目前是使用ZK实现服务注册以及自增ID的获取，也可以使用MySQL、Redis使用，支持降级的方案配置
- 配置多个zk，客户端配置多个zk集群，服务端自己去配置datacenter
- 基于Netty、rpc框架实现