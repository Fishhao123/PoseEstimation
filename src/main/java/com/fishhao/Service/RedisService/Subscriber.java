package com.fishhao.service.RedisService;

import com.fishhao.controller.WebSocketHelper;
import com.fishhao.entity.MathEntity.PointEntity;
import com.fishhao.utils.PoseUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.HashMap;

//Redis订阅者类，里面是各种回调方法
public class Subscriber extends JedisPubSub{
    public Subscriber(){}

    @Override
    public void onMessage(String channel, String message) {       //收到消息会调用
        //System.out.println(String.format("receive redis published message, channel %s, message %s", channel, message));
        //TODO:解析message，获取userId
        if(StringUtils.isNotBlank(channel) && channel.equals("yanchuanChannel")){
            JSONObject json = JSONObject.fromObject(message.toString());
            String imageID = json.getString("imageID");

            Jedis jedis = getJedis();

            String userToken = jedis.hget(imageID,"userToken");//  user
            String poseResultString = jedis.hget(imageID,"poseResult"); //  pose
            String width = jedis.hget(imageID,"width");
            String height = jedis.hget(imageID,"height");

            ArrayList<HashMap<Integer,PointEntity>> result = PoseUtils.getPoseData(poseResultString,Float.parseFloat(width),Float.parseFloat(height));

            WebSocketHelper.sendData(result, userToken);
            jedis.close();
        }


    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {    //订阅了频道会调用
        System.out.println(String.format("subscribe redis channel success, channel %s, subscribedChannels %d", channel, subscribedChannels));
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {   //取消订阅会调用
        System.out.println(String.format("unsubscribe redis channel, channel %s, subscribedChannels %d", channel, subscribedChannels));
    }

    private Jedis getJedis(){
        Jedis jedis = null;
        JedisPool jedisPool = WebSocketHelper.getJedisPool();
        if(jedisPool!=null){
            jedis = jedisPool.getResource();
        }
        return jedis;
    }
}