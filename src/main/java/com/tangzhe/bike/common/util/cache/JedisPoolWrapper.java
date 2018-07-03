package com.tangzhe.bike.common.util.cache;

import com.tangzhe.bike.common.constants.Parameters;
import com.tangzhe.bike.common.exception.BikeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class JedisPoolWrapper {

    @Autowired
    private Parameters parameters;

    private JedisPool jedisPool = null;

    @PostConstruct
    public void init() throws BikeException {
        try {
            JedisPoolConfig config  = new JedisPoolConfig();
            config.setMaxWaitMillis(parameters.getRedisMaxWaitMillis());
            config.setMaxIdle(parameters.getRedisMaxIdle());
            config.setMaxTotal(parameters.getRedisMaxTotal());
            jedisPool = new JedisPool(config, parameters.getRedisHost(), parameters.getRedisPort(),2000);
        } catch (Exception e) {
           log.error("Fail to init redis pool", e);
           throw new BikeException("初始化redis失败");
        }
    }

    public JedisPool getJedisPool(){
        return jedisPool;
    }

}
