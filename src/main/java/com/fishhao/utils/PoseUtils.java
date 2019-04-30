package com.fishhao.utils;

import com.fishhao.entity.MathEntity.PointEntity;
import com.fishhao.entity.MathEntity.VectorEntity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by AndrewKing on 4/29/2019.
 */
public class PoseUtils{

    /**
     * This static method inputs 结果pose Json 的 String, 原始图片的宽，高
     * @return ArrayList<HashMap<Integer,PointEntity>> 图片中每个人的每个关节点的位置
     */
    public static ArrayList<HashMap<Integer,PointEntity>> getPoseData (String poseResultString, float width, float height) {
        //返回的结果
        ArrayList<HashMap<Integer,PointEntity>> poseList = new ArrayList<HashMap<Integer, PointEntity>>();

        JSONArray poseArray = JSONArray.fromObject(poseResultString);
        if (!poseArray.isEmpty()) {
            //对每个人的获取
            for (int i = 0; i < poseArray.size(); i++) {
                JSONObject human = poseArray.getJSONObject(i);
                JSONObject bodyParts = JSONObject.fromObject(human.get("body_parts"));

                Set keySet = bodyParts.keySet();
                HashMap<Integer,PointEntity> centerMap = new HashMap<Integer,PointEntity>();// 中心点的集合
                // draw point
                for(int j=0; j<CocoConstants.Background; j++){
                    if(!keySet.contains(String.valueOf(j))) {
                        continue;
                    }
                    JSONObject bodyPart = bodyParts.getJSONObject(String.valueOf(j));
                    // 画图
                    float x = Float.parseFloat(JSONArray.fromObject(JSONObject.fromObject(bodyPart.get("x")).get("py/newargs")).get(0).toString());
                    float y = Float.parseFloat(JSONArray.fromObject(JSONObject.fromObject(bodyPart.get("y")).get("py/newargs")).get(0).toString());
                    float confidence = 1.0f;

                    PointEntity center = new PointEntity(x*width+0.5f, y*height+0.5f,confidence);
                    centerMap.put(j,center);
                }
                poseList.add(centerMap);
            }
        }
        return poseList;
    }

    /**
     * This static method inputs three point
     * @return int angle.
     */
    public static int getAngle(PointEntity Px, PointEntity Py, PointEntity Pz) {
//        Px is the center point
        double x1 = Px.x;
        double x2 = Px.y;
        double y1 = Py.x;
        double y2 = Py.y;
        double z1 = Pz.x;
        double z2 = Pz.y;

        //向量的点乘
        double t =(y1-x1)*(z1-x1)+(y2-x2)*(z2-x2);

        // A=向量的点乘/向量的模相乘
        // B=arccos(A)，用反余弦求出弧度
        // result=180*B/π 弧度转角度制
        int result =(int)(180*Math.acos(
                t/Math.sqrt
                        ((Math.abs((y1-x1)*(y1-x1))+Math.abs((y2-x2)*(y2-x2)))
                                *(Math.abs((z1-x1)*(z1-x1))+Math.abs((z2-x2)*(z2-x2)))
                        ))
                /Math.PI);
        //      pi   = 180
        //      x    =  ？
        //====> ?=180*x/pi
        return result;
    }

    /**
     * This static method inputs three point
     * 两个不相交的向量，通用的方法
     * @return return Cos Similarity
     */
    public static int getCosSimilar(VectorEntity v1, VectorEntity v2){
        float dx1 = v1.p2.x - v1.p1.x;
        float dx2 = v2.p2.x - v2.p1.x;
        float dy1 = v1.p2.y - v1.p1.y;
        float dy2 = v2.p2.y - v2.p1.y;

        //弧度：
        double result = Math.acos(  (dx1*dx2+dy1*dy2)/Math.sqrt(dx1*dx1+dx2*dx2+dy1*dy1+dy2*dy2) );
        //相似度：
        int cosSimilarity = (int)(result/Math.PI);
        return cosSimilarity;
    }
}