package com.tangzhe.bike.common.vo;

import lombok.Data;

/**
 * Created by 唐哲
 * 2018-06-02 12:57
 * 安卓端发送过来的登录数据
 */
@Data
public class UserInfo {

    /** 登录信息密文 */
    private String data;

    /** RSA加密的AES的密钥 */
    private String key;

}
