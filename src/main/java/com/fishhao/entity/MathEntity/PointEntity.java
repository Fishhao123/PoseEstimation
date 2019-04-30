package com.fishhao.entity.MathEntity;

/**
 * Created by AndrewKing on 4/30/2019.
 */
public class PointEntity {
    float x;
    float y;
    float confidence;
    public PointEntity(float x, float y, float confidence){
        this.x=x;
        this.y=y;
        this.confidence=confidence;
    }
}