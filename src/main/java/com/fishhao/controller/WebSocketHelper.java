package com.fishhao.controller;

import com.alibaba.fastjson.JSON;
import com.fishhao.entity.SendMsg;
import com.fishhao.service.RedisService.SubThread;
import com.fishhao.service.RedisService.SubThread;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//import javax.jms.BytesMessage;
//import javax.jms.JMSException;
//import javax.jms.Message;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AndrewKing on 5/5/2019.
 */
@ServerEndpoint(value = "/webSocket/{userToken}")
public class WebSocketHelper {
//公有的属性
    private static int onlineCount = 0; //用于记录当前在线连接数

    //线程安全，用来存放每个客户端对应的WebSocket对象（userToken-webSocket键值对形式）。
    private static ConcurrentHashMap<String, WebSocketHelper> userWebSocketMap = new ConcurrentHashMap<String, WebSocketHelper>();

    private static volatile JedisPool jedisPool;

//私有的属性
    private Session session; //连接会话
    private String userToken; //用户Id


    //连接建立调用方法
    @OnOpen
    public void onOpen(@PathParam("userToken") String userToken, Session session){
        webSocketInit(userToken, session);
//        redisInit();
    }

    //接收到消息调用方法
    @OnMessage
    public void onMessage(String message) { //TODO:接收到前台的实时姿态数据后存入redis
        System.out.println("接收到消息： " + message);
        SendMsg sendMsg = JSON.parseObject(message, SendMsg.class);
//        putRedis(message);
    }

//    @OnMessage
//    public void onBinaryMessage(ByteBuffer message, Session session, boolean last) throws IOException, InterruptedException {
//        byte [] sentBuf = message.array();
//
//        System.out.println("Binary Received: " + sentBuf.length + ", last: " + last);
//
//        //下面的代码逻辑，是用UDP协议发送视频流数据到视频处理服务器做后续逻辑处理
//        //sendToVideoRecognizer(sentBuf);
//    }

    //连接关闭调用方法
    @OnClose
    public void onClose(){
        userWebSocketMap.remove(this);
        subOnlineCount();
        System.out.println("有一个连接关闭，当前在线人数为" + getOnlineCount());
    }

    //发生错误时调用的方法
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误！");
        error.printStackTrace();
    }

    //websocket初始化方法
    public synchronized void webSocketInit(String userToken, Session session){
        this.userToken = userToken;
        this.session = session;
        addOnlineCount();
        System.out.println("有一个连接开启，当前在线人数为" + getOnlineCount());
        if(!userWebSocketMap.containsKey(userToken)){
            userWebSocketMap.put(userToken, this);
        }
    }

    //redis初始化方法, 需要将这个websocket实例传入
    public synchronized void redisInit(){
        if(jedisPool!=null){
            jedisPool = new JedisPool(new JedisPoolConfig(), "10.103.238.165", 6379, 10, "admin");
        }
        SubThread subThread = new SubThread(jedisPool,"yanchuangChannel");
        subThread.start(); //进行订阅
    }

    //返回jedisPool实例
    public static synchronized JedisPool getJedisPool(){
        return jedisPool;
    }

    //读取在线连接
    public static synchronized int getOnlineCount(){
        return onlineCount;
    }

    //增加在线连接
    public static synchronized void addOnlineCount(){
        WebSocketHelper.onlineCount++;
    }

    //减少在线连接
    public static synchronized void subOnlineCount(){
        WebSocketHelper.onlineCount--;
    }

    //发送函数，输入参数是ArrayList
    // 1. synchronized：需要保证线程安全
    // 2. static的目的：自动获取WebSocketHelper的实例
    public static synchronized void sendData(ArrayList returnData, String userToken){
        //STEP1: TODO:此处应当先解析returnData（json）

        //STEP2: 发送
        try {
            WebSocketHelper webSocketHelper = userWebSocketMap.get(userToken);
            if(webSocketHelper!=null){
                webSocketHelper.session.getBasicRemote().sendObject(returnData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //收到消息后放入redis中
    //FIXME： 不知道websocket的数据返回类型具体是啥样的，先写一个类似于JMS的方法替代
//    public static synchronized void putRedis(Message message){
//        BytesMessage bytesMessage = (BytesMessage)message;
//        try {
//            // 得到一些参数：
//
//            String user_token = String.valueOf(bytesMessage.getByteProperty("user-token"));  // user-token
//            String task = String.valueOf(bytesMessage.getByteProperty("task"));               // task
//            String image_base64 ="";                                            // origin image
//            UUID uuid= UUID.randomUUID();
//            String imageID = uuid.toString();                                   // imageID
//
//            byte[] buffer = new byte[1024*1024];
//            int len = 0;
//            while((len=bytesMessage.readBytes(buffer))!=-1){
//                image_base64 = new String(buffer,0,len);
//            }
//
////                System.out.println("imageID: "+imageID +",user-token: "+user_token+" ,task: "+task+", image_base64: "+ image_base64);
//            Jedis jedis = jedisPool.getResource();
//
//
//            Map<String,String> imageData = new HashMap<String, String>();
//            imageData.put("imageID",imageID);
//            imageData.put("userToken",user_token);
//            imageData.put("taskList",task);
//            imageData.put("image",image_base64);
//
//            JSONObject jsonObject = JSONObject.fromObject(imageData);
//
////              比较task列表并分发存入对应的redis的list
//
//            int taskToDo = Integer.parseInt(task);
//            if((taskToDo&0x01)>0) { //pose
//                jedis.rpush("image_queue_to_pose_estimation", jsonObject.toString());
////                    jedisTemplate.convertAndSend("test_channel",jsonObject);
//            }
//            if((taskToDo&0x02)>0){ //face
//                jedis.rpush("image_queue_to_face_recognition", jsonObject.toString());
//            }
//            if((taskToDo&0x04)>0){ //object
//                jedis.rpush("image_queue_to_object_recognition", jsonObject.toString());
//            }
//
//            //手动释放资源，不然会因为jedisPool里面的maxActive=200的限制，只能创建200个jedis资源。
//            jedis.close();
//
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }
}
