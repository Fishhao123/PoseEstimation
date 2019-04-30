package com.fishhao.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MatchingUtil {
    private int[] vector1;
    private int[] vector2;

    public MatchingUtil(int[] vector1, int[] vector2){
        this.vector1 = vector1;
        this.vector2 = vector2;
    }

    Map<Character, int[]> vectorMap = new HashMap<Character, int[]>();

    // 求余弦相似度
    public double sim() {
        return this.pointMulti()/this.sqrtMulti();
    }

    //点乘运算
    private double pointMulti() {
        return this.vector1[0] * this.vector2[0] + this.vector1[1] * this.vector2[1];
    }

    private double sqrtMulti() {
        double result1 = 0;
        double result2 = 0;
        for(int i = 0; i < this.vector1.length-1 ; i++){ //第三个坐标代表置信度，平方和运算不取
            result1 += this.vector1[i] * this.vector1[i];
            result2 += this.vector2[i] * this.vector2[i];
        }
        return Math.sqrt(result1 * result2);

    }






}
