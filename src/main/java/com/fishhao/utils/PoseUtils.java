package com.fishhao.utils;

import com.fishhao.entity.MathEntity.PointEntity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by AndrewKing on 4/29/2019.
 */
public class PoseUtils {

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
}