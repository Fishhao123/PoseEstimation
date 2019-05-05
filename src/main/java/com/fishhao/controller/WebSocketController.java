package com.fishhao.controller;

import com.fishhao.Service.RedisService.SubThread;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/webSocket")
public class WebSocketController {
    private static int onlineCount = 0; //用于记录当前在线连接数
    private Session session; //连接会话

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标
    private static CopyOnWriteArraySet<WebSocketController> webSocketSet = new CopyOnWriteArraySet<WebSocketController>();

    //连接建立调用方法
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        System.out.println("有新连接加入，当前在线人数为" + getOnlineCount());
    }

    //接收到消息调用方法
    @OnMessage
    public void onMessage(String message) {
        System.out.println("接收到消息： " + message);
        //连接redis服务端
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "10.103.238.165", 6379, 10, "admin");
        SubThread subThread = new SubThread(jedisPool,"unity");
        subThread.start(); //进行订阅
//        readData("F:/UnityProjects/pos_sample1.txt");
    }

    //连接关闭调用方法
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("有一个连接关闭，当前在线人数为" + getOnlineCount());
    }

    //发生错误时调用的方法
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误！");
        error.printStackTrace();
    }
//
//    //读取本地存放的姿态数据
//    public void readData(String fileName){
//        try{
//            FileReader fileReader = new FileReader(fileName);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            String data = null;
//            while ((data = bufferedReader.readLine()) != null){
//                sendMessage(data);
//                try{
//                    Thread.sleep(33); //每33ms读取发送一次
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//            }
//            bufferedReader.close();
//            fileReader.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//    }

    //发送函数
    public synchronized void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    //读取在线连接
    public static synchronized int getOnlineCount(){
        return onlineCount;
    }

    //增加在线连接
    public static synchronized void addOnlineCount(){
        WebSocketController.onlineCount++;
    }

    //减少在线连接
    public static synchronized void subOnlineCount(){
        WebSocketController.onlineCount--;
    }

    public synchronized void sendData(String returnData){
        try {
            for (WebSocketController item : webSocketSet) {
                item.sendMessage(returnData); //向前台发送数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
