package com.fishhao.entity;

//前台向后台发送信息的实体类
public class SendMsg {
    private String userToken; //用户标识
    private String image; //经过base64编码后的视频帧
    private int task; //用于指定算法，可选1,2,3

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }
}
