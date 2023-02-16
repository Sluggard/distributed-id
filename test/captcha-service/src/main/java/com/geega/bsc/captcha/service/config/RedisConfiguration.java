package com.geega.bsc.captcha.service.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * 配置自适应集群拓扑刷新和静态刷新源
 *
 * @author Jun.An3
 * @date 2022/05/24
 */
@Configuration
public class RedisConfiguration {

    /**
     * 模式(single:单机,cluster:集群)
     */
    @Value("${spring.redis.model:single}")
    private String model;

    /**
     * 单机节点配置
     */
    @Value("${spring.redis.host:}")
    private String host;

    @Value("${spring.redis.database:0}")
    private String database;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.port:6379}")
    private String port;

    /**
     * cluster配置
     */
    @Value("${spring.redis.cluster.max-redirects:3}")
    private String maxRedirects;

    @Value("${spring.redis.cluster.nodes:}")
    private String clusterNodes;

    /**
     * 连接池配置
     */
    @Value("${spring.redis.lettuce.pool.max-active:}")
    private Integer maxActive;

    @Value("${spring.redis.lettuce.pool.min-idle:}")
    private Integer minIdle;

    @Value("${spring.redis.lettuce.pool.max-idle:}")
    private Integer maxIdle;

    @Value("${spring.redis.lettuce.pool.max-wait:}")
    private Long maxWaitMillis;

    @Value("${spring.redis.lettuce.pool.time-between-eviction-runs:}")
    private Long timeBetweenEvictionRuns;

    private static final String SINGLE = "single";

    @Bean(destroyMethod = "destroy")
    public LettuceConnectionFactory lettuceConnectionFactory() {

        //配置对象
        GenericObjectPoolConfig<?> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMinIdle(minIdle);
        if (timeBetweenEvictionRuns != null) {
            genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRuns);
        }
        if (maxWaitMillis != null) {
            genericObjectPoolConfig.setMaxWaitMillis(maxWaitMillis);
        }

        if (SINGLE.equalsIgnoreCase(model)) {
            // 单机redis
            LettucePoolingClientConfiguration lettucePoolingClientConfiguration = LettucePoolingClientConfiguration
                    .builder()
                    .poolConfig(genericObjectPoolConfig)
                    .build();
            RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
            redisConfig.setHostName(host);
            redisConfig.setPort(Integer.parseInt(port));
            redisConfig.setDatabase(Integer.parseInt(database));
            redisConfig.setPassword(password);
            return new LettuceConnectionFactory(redisConfig, lettucePoolingClientConfiguration);
        } else {
            //开启 自适应集群拓扑刷新和周期拓扑刷新
            ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                    // 开启全部自适应刷新
                    // 开启自适应刷新,自适应刷新不开启,Redis集群变更时将会导致连接异常
                    .enableAllAdaptiveRefreshTriggers()
                    // 自适应刷新超时时间(默认30秒)
                    // 默认关闭开启后时间为30秒
                    .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(30))
                    // 开周期刷新
                    // 默认关闭开启后时间为60秒 ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD 60  .enablePeriodicRefresh(Duration.ofSeconds(2)) = .enablePeriodicRefresh().refreshPeriod(Duration.ofSeconds(2))
                    .enablePeriodicRefresh(Duration.ofSeconds(20))
                    .build();
            ClientOptions clientOptions = ClusterClientOptions.builder()
                    .topologyRefreshOptions(clusterTopologyRefreshOptions)
                    .build();
            LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                    .poolConfig(genericObjectPoolConfig)
                    // .readFrom(ReadFrom.MASTER_PREFERRED)
                    .clientOptions(clientOptions)
                    .build();

            Set<RedisNode> nodes = new HashSet<>();
            String[] clusterList = clusterNodes.split(",");
            for(String sen : clusterList) {
                String[] arr = sen.split(":");
                nodes.add(new RedisNode(arr[0],Integer.parseInt(arr[1])));
            }
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
            clusterConfiguration.setClusterNodes(nodes);
            clusterConfiguration.setPassword(RedisPassword.of(password));
            clusterConfiguration.setMaxRedirects(Integer.parseInt(maxRedirects));

            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(clusterConfiguration, clientConfig);
            //是否允许多个线程操作共用同一个缓存连接，默认true，false时每个操作都将开辟新的连接
            lettuceConnectionFactory.setShareNativeConnection(false);
            //重置底层共享连接, 在接下来的访问时初始化
            lettuceConnectionFactory.resetConnection();
            return lettuceConnectionFactory;
        }
    }

}
