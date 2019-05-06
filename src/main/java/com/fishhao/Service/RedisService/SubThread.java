package com.fishhao.service.RedisService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SubThread extends Thread {

    private final JedisPool jedisPool;
    private final Subscriber subscriber = new Subscriber();

    private final String channel;

    public SubThread(JedisPool jedisPool, String channel) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.channel = channel;
    }

    @Override
    public void run() {
        System.out.println(String.format("subscribe redis, channel %s, thread will be blocked", channel));
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();   //取出一个连接
            jedis.subscribe(subscriber, channel);    //通过subscribe 的api去订阅，入参是订阅者和频道名

        } catch (Exception e) {
            System.out.println(String.format("subsrcibe channel error, %s", e));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}