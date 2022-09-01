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
基于spring-boot-starter模块，提供HTTP方式获取ID的应用，包含distributed-id-common、distributed-id-client、id-spring-boot-starter模块
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
id.cache.capacity=300
# ID缓存少于trigger时，会触发拉取操作，拉取数量为capacity - trigger
id.cache.trigger=220
# zk命名空间
id.zk.namespace=id
# zk的连接，集群时，例子：127.0.0.1:2181，127.0.0.1:2182
id.zk.connection=127.0.0.1:2181
# zk会话超时
id.zk.sessionTimeoutMs=10000
# zk连接超时
id.zk.connectionTimeoutMs=10000
```

##### 如何解决NIO编程存在的问题
- 空轮询
- 半包
- 粘包
- 缓冲溢出
- 断开连接
- 网络异常
- 重连
- 代码凌乱

##### 待办事项

- 基于Netty实现
