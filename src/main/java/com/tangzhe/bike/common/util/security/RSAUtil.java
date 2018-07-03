package com.tangzhe.bike.common.util.security;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 非对称加密解密工具类
 */
public class RSAUtil {

    /*** 公钥字符串 */
    private static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCuVRY8B3+Af5euC9WbgNkJKAiBzqOvrYi9mSST78jd4clpn7vkYHDfHzJiqFz9wjNRLzg9MUREF53bw9yhSljZ7F8JPMryfe8RR2Ed6CJq5nCy/2hvTTw4L6ypDemwe9f9yjIg52oPRPwU8lm8Uj3wKhjedDmZrkO1TAmt3sbQtwIDAQAB";

    /*** 私钥字符串 */
    private static String PRIVATE_KEY = "MIICXAIBAAKBgQCuVRY8B3+Af5euC9WbgNkJKAiBzqOvrYi9mSST78jd4clpn7vkYHDfHzJiqFz9wjNRLzg9MUREF53bw9yhSljZ7F8JPMryfe8RR2Ed6CJq5nCy/2hvTTw4L6ypDemwe9f9yjIg52oPRPwU8lm8Uj3wKhjedDmZrkO1TAmt3sbQtwIDAQABAoGAHvJ32fwyxCriUEFFkC9VV+zFZdW69QrHRXEULzbX35ufV6LFhgsgvlsZZK4io+F/np/VSfee+L+AhGzGk9NQ5kOC5fe6AgsmWKvHt67eGx8P08TLrsx3moSyno/2tBiH9v3CGOltNTSLJwbIb5G6R8zya7ld5+67yp8y+RMhhGECQQDiVAgcm6QbU//O3fcYHyojNolDUMwJ7eBxbuxbPkSnCfAww5rQU9ayH4PComHbY9h+3yUbWSsm3aCTcdNFY/2pAkEAxS/7vi1rOsThdEdmN3A4J7kFxAaowr8/fNGfmF5Q9WoIBCxoWcH5VpsA1qdqFXakg7NZlqEeSVorMBYcCcWXXwJBAM013/0AafWVXhYVT9uBNlWjNyXf9oDyPFTdfFTmypyh+DRextopijsLNA6f6RZhG4U9komPef7NaLEHvcXm+bECQGIhwg/f/JhPuL+sdMMsNtYV7zeh9MSOduMAU4N5lKK9tOWW0AuzTAn8s8sfn9y0oaTlUbVz02W/2PRiXGvEiNMCQEVUsTwJGPm89MYlD28x4wceX7Mv9C4GB/6eTSYC9mThHzoYt+228WITQ96zoN1l4Cu4uml7fWfyW42OGMB7qUs=";

    public static String getPublicKey() {
        return PUBLIC_KEY;
    }

    public static String getPrivateKey() {
        return PRIVATE_KEY;
    }

    private static final String KEY_ALGORITHM = "RSA";

    private RSAUtil() {}

    private static RSAUtil instance;

    public static RSAUtil getInstance() {
        if(instance == null) {
            instance = new RSAUtil();
        }
        return instance;
    }

    /**
     * 公钥加密
     */
    public byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64Util.getInstance().decode(key);
        X509EncodedKeySpec pkcs8KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 引入第三方密码工具包 处理编码
     */
    public static PrivateKey makePrivateKey(String stored) throws Exception {
        byte[] data = Base64Util.getInstance().decode(stored);
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(0));
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()));
        v2.add(DERNull.INSTANCE);
        v.add(new DERSequence(v2));
        v.add(new DEROctetString(data));
        ASN1Sequence seq = new DERSequence(v);
        byte[] privKey = seq.getEncoded("DER");
        PKCS8EncodedKeySpec spec = new  PKCS8EncodedKeySpec(privKey);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey key = fact.generatePrivate(spec);
        return key;
    }

    /**
     * 私钥解密
     */
    public byte[] decryptByPrivateKey(byte[] data) throws Exception {
        Key privateKey = makePrivateKey(PRIVATE_KEY);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static void main(String[] args) throws Exception {
//        // 公钥加密
//        byte[] encrypt = RSAUtil.getInstance().encryptByPublicKey("老王来了...".getBytes("utf-8"), PUBLIC_KEY);
//        System.out.println(encrypt.toString());
//        // 私钥解密
//        byte[] decrypt = RSAUtil.getInstance().decryptByPrivateKey(encrypt);
//        System.out.println(new String(decrypt, "UTF-8"));

        // 移动端随机生成的用于对称加密的key，需要进行非对称加密发送给后端
        String key = "123456789abcdefg";
        // 移动端的数据，需要使用key对其进行对称加密，发送给后端
        String data = "{'mobile':'18852937197','code':'6666','platform':'android','channelId':12454348}";
        String encryptData = AESUtil.getInstance().encrypt(data, key);
        System.out.println(encryptData); // 加密后的data
        // key非对称加密
        byte[] encryptKeyBytes = RSAUtil.getInstance().encryptByPublicKey(key.getBytes("UTF-8"), RSAUtil.getPublicKey());
        String encryptKey = Base64Util.getInstance().encode(encryptKeyBytes);
        System.out.println(encryptKey); // 加密后的key

        // 服务端非对称解密key，然后利用解密后的key对称解密data
        byte[] decryptBytes = RSAUtil.getInstance().decryptByPrivateKey(encryptKeyBytes);
        String decryptKey = new String(decryptBytes, "UTF-8");
        String decryptData = AESUtil.getInstance().decrypt(encryptData, decryptKey);
        System.out.println(decryptData);
    }

}
