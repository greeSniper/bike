package com.tangzhe.bike.common.util.security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5工具类
 **/
public class MD5Util {

    private MD5Util() {}

    private static MD5Util instance;

    public static MD5Util getInstance() {
        if(instance == null) {
            instance = new MD5Util();
        }
        return instance;
    }

    public String getMD5(String source){
        return DigestUtils.md5Hex(source);
    }

    public static void main(String[] args) {
        String md5Str = MD5Util.getInstance().getMD5("123456");
        System.out.println(md5Str);
    }

}
