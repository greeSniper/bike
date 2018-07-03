package com.tangzhe.bike.common.util.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 对称加密解密工具类
 */
public class AESUtil {
    
    public static final String KEY_ALGORITHM = "AES";
    public static final String KEY_ALGORITHM_MODE = "AES/CBC/PKCS5Padding";

    private AESUtil() {}

    private static AESUtil instance;

    public static AESUtil getInstance() {
        if(instance == null) {
            instance = new AESUtil();
        }
        return instance;
    }

    /**
     * AES对称加密
     * @param data {'mobile':'18980840843','code':'8060','platform':'android','channelId':12454348}
     * @param key key需要16位 123456789abcdefg
     * @return GVa1GfXHZCRLfopSD7E6vDAP8vVKkuF8C0P7JdNZmI/JDc/tXYZYNH3M/Sjk ...
     */
    public String encrypt(String data, String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes("UTF-8"),KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.ENCRYPT_MODE , spec,new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] bs = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64Util.getInstance().encode(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
    
    /**
     * AES对称解密
     * @param data GVa1GfXHZCRLfopSD7E6vDAP8vVKkuF8C0P7JdNZmI/JDc/tXYZYNH3M/Sjk ...
     * @param key key需要16位 123456789abcdefg
     * @return {'mobile':'18980840843','code':'8060','platform':'android','channelId':12454348}
     */
    public String decrypt(String data, String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes("UTF-8"), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.DECRYPT_MODE , spec , new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] originBytes = Base64Util.getInstance().decode(data);
            byte[] result = cipher.doFinal(originBytes);
            return new String(result,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


    public static void main(String[] args) throws Exception {
        // 移动端随机生成的用于对称加密的key，需要进行非对称加密发送给后端
        String key = "123456789abcdefg";
        // 移动端的数据，需要使用key对其进行对称加密，发送给后端
        String data = "{'mobile':'18980840843','code':'8060','platform':'android','channelId':12454348}";
        String encrypt = AESUtil.getInstance().encrypt(data, key);
        System.out.println(encrypt);

        String decrypt = AESUtil.getInstance().decrypt(encrypt, key);
        System.out.println(decrypt);
    }
}
