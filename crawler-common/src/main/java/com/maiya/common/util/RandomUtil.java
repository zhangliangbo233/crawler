package com.maiya.common.util;

import java.util.Random;

/**
 * 随机数工具类
 * @author xiangdf
 *
 */
public class RandomUtil {
	
    /**
     * 生成指定范围的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }



    

}
