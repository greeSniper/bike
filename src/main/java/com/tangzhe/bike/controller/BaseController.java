package com.tangzhe.bike.controller;

import com.tangzhe.bike.common.constants.Constants;
import com.tangzhe.bike.common.util.cache.JedisUtil;
import com.tangzhe.bike.entity.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 唐哲
 * 2018-06-05 15:51
 */
@Slf4j
public class BaseController {

    @Autowired
    private JedisUtil jedisUtil;

    /**
     * 从请求头获取token
     * 利用tokon从redis中获取userElement
     */
    protected UserElement getCurrentUser(){
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader(Constants.REQUEST_TOKEN_KEY);
        if(!StringUtils.isBlank(token)){
            try {
                UserElement ue = jedisUtil.getUserByToken(token);
                return ue;
            }catch (Exception e){
                log.error("fail to get user by token", e);
                throw e;
            }
        }
        return null;
    }

    // 获取请求ip
    protected String getIpFromRequest(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
    }

}
