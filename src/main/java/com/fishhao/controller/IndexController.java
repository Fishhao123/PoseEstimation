package com.fishhao.controller;

import com.alibaba.fastjson.JSONObject;
import com.fishhao.entity.SubThread;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Controller
public class IndexController {
    @RequestMapping("/webGL")
    public ModelAndView webGL(){
        // 连接redis服务端
        //JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "10.103.238.165", 6379, 10, "admin");
        //SubThread subThread = new SubThread(jedisPool,"unity");
        //subThread.start(); //进行订阅
        return new ModelAndView("webGL");
    }

    @RequestMapping("/test")
    public ModelAndView test(){
        // 连接redis服务端
        //JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "10.103.238.165", 6379, 10, "admin");
        //SubThread subThread = new SubThread(jedisPool,"unity");
        //subThread.start(); //进行订阅
        return new ModelAndView("test");
    }

    @RequestMapping(value = "/sendWS", method = {RequestMethod.POST}) //用于获取inflexDB中tags（key-value）和fields（key）
    @ResponseBody
    public void sendWS(HttpServletRequest request) {
//        String msg = request.getParameter("msg");
        WebSocketController wsc = new WebSocketController();
        try{
            FileReader fileReader = new FileReader("F:/UnityProjects/example.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String data = null;
            while ((data = bufferedReader.readLine()) != null){
                wsc.sendData(data); //通过WebSocket向前端发送数据
                try{
                    Thread.sleep(33); //每33ms读取发送一次
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            bufferedReader.close();
            fileReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
