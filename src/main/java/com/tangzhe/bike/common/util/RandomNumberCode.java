package com.tangzhe.bike.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * Created by tangzhe 2017/9/11.
 */
public class RandomNumberCode {

    private RandomNumberCode() {}

    private static RandomNumberCode instance;

    public static RandomNumberCode getInstance() {
        if(instance == null) {
            instance = new RandomNumberCode();
        }
        return instance;
    }

    /**
     * 4位随机验证码
     */
    public String verCoder() {
        Random random = new Random();
        return StringUtils.substring(String.valueOf(random.nextInt()),2,6);
    }

    /**
     * 随机单车订单号
     */
    public String randomNo() {
        Random random = new Random();
        return String.valueOf(Math.abs(random.nextInt()*-10));
    }

    public static void main(String[] args) {
        String verCode = RandomNumberCode.getInstance().verCoder();
        String randomNo = RandomNumberCode.getInstance().randomNo();
        System.out.println(verCode);
        System.out.println(randomNo);
    }

}
