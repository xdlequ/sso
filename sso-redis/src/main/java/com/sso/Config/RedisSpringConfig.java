package com.sso.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ql on 2019/7/1.
 */


@Configuration
@PropertySource(value = "classpath:redis.properties")
public class RedisSpringConfig {

    @Value("${redis.maxTotal}")
    private Integer redisMaxTotal;

    @Value("${redis.node.host}")
    private String redisNodeHost;

    @Value("${redis.node.port}")
    private Integer redisNodePort;



    private JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisMaxTotal);
        return jedisPoolConfig;
    }


    @Bean
    public JedisPool getJedisPool(){
        JedisPool jedisPool=new JedisPool(jedisPoolConfig(),redisNodeHost,redisNodePort);
        return jedisPool;
    }
    /**
     * SharedJedis是基于一致性哈希算法实现的分布式Redis集群客户端
     * sharedJedis的设计分为以下几块
     * 1.对象池设计：Pool，sharedJedisPool,sharedJedisFactory
     * @return
     */
    @Bean
    public ShardedJedisPool shardedJedisPool() {

        List<JedisShardInfo> jedisShardInfos = new ArrayList<JedisShardInfo>();
        jedisShardInfos.add(new JedisShardInfo(redisNodeHost, redisNodePort));
        return new ShardedJedisPool(jedisPoolConfig(), jedisShardInfos);
    }

}
