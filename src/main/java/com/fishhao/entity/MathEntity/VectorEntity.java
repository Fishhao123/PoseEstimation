package com.fishhao.entity.MathEntity;

/**
 * Created by AndrewKing on 4/30/2019.
 */
public class VectorEntity {
    //p1指向p2
    PointEntity p1;
    PointEntity p2;

    public VectorEntity(PointEntity p1, PointEntity p2){
        this.p1=p1;
        this.p2=p2;
    }

    //return Cos Similarity
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