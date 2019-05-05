package com.fishhao.utils;

import com.fishhao.Service.RedisService.SubThread;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class PubSubDemo {

    public static void main( String[] args )
    {
        // 连接redis服务端
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "10.103.238.165", 6379, 10, "admin");

        System.out.println(String.format("redis pool is starting, redis ip %s, redis port %d", "10.103.238.165", 6379));

        SubThread subThread = new SubThread(jedisPool, "unity");  //订阅者
        subThread.start();

//        Publisher publisher = new Publisher(jedisPool);    //发布者
//        publisher.start();
    }
}