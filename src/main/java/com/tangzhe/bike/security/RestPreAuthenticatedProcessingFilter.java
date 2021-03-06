package com.tangzhe.bike.security;

import com.tangzhe.bike.common.constants.Constants;
import com.tangzhe.bike.common.util.cache.JedisUtil;
import com.tangzhe.bike.entity.UserElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 预授权过滤器
 */
public class RestPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    private Logger log = LoggerFactory.getLogger(RestPreAuthenticatedProcessingFilter.class);

    /**
     * spring的路径匹配器
     */
    private AntPathMatcher matcher = new AntPathMatcher();

    private List<String> noneSecurityList;

    private JedisUtil jedisUtil;

    public RestPreAuthenticatedProcessingFilter(List<String> noneSecurityList, JedisUtil jedisUtil) {
        this.noneSecurityList = noneSecurityList;
        this.jedisUtil = jedisUtil;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        GrantedAuthority[] authorities = new GrantedAuthority[1];
        if(isNoneSecurity(request.getRequestURI().toString())|| "OPTIONS".equals(request.getMethod())){
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_SOMEONE");
            authorities[0] = authority;
            return new RestAuthenticationToken(Arrays.asList(authorities));
        }
        //检查APP版本
        String version = request.getHeader(Constants.REQUEST_VERSION_KEY);
        String token = request.getHeader(Constants.REQUEST_TOKEN_KEY);
        if (version == null) {
            request.setAttribute("header-error", 400);
        }
        //检查token
        if(request.getAttribute("header-error") == null){
            try {
                if(!StringUtils.isBlank(token)){
                    UserElement ue = jedisUtil.getUserByToken(token);
                    if (ue instanceof UserElement) {
                        //检查到token说明用户已经登录 授权给用户BIKE_CLIENT角色 允许访问
                        GrantedAuthority authority = new SimpleGrantedAuthority("BIKE_CLIENT");
                        authorities[0] = authority;
                        RestAuthenticationToken authToken = new RestAuthenticationToken(Arrays.asList(authorities));
                        authToken.setUser(ue);
                        return authToken;
                    }else {
                        //token不对
                        request.setAttribute("header-error", 401);
                    }
                } else {
                    log.warn("Got no token from request header");
                    //token不存在 告诉移动端 登录
                    request.setAttribute("header-error", 401);
                }
            }catch (Exception e){
                log.error("Fail to authenticate user", e);
            }
        }
        if(request.getAttribute("header-error") != null){
            //请求头有错误  随便给个角色 让逻辑继续
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_NONE");
            authorities[0] = authority;
        }
        RestAuthenticationToken authToken = new RestAuthenticationToken(Arrays.asList(authorities));
        return authToken;
    }

    /**
     * 判断该uri是否为不需要拦截的uri
     * login,regester,sendVercode,generateBike
     * 这些都不需要拦截，未登录也能访问
     */
    private boolean isNoneSecurity(String uri) {
        boolean result = false;
        if (this.noneSecurityList != null) {
            for (String pattern : this.noneSecurityList) {
                if (matcher.match(pattern, uri)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return null;
    }

}
