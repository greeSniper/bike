package com.tangzhe.bike;

import com.tangzhe.bike.common.constants.Parameters;
import com.tangzhe.bike.common.util.cache.JedisUtil;
import com.tangzhe.bike.sms.SmsProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by 唐哲
 * 2018-06-02 11:19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BikeApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private Parameters parameters;

    @Autowired
    private SmsProcessor smsProcessor;

    //@Test
    public void testUserList() {
        String result = restTemplate.getForObject("/user/list", String.class);
        System.out.println(result);
        System.out.println(port);
    }

    //@Test
    public void testParams() {
        String result = restTemplate.getForObject("/user/params", String.class);
        System.out.println(result);
    }

    @Test
    public void testCache() {
//        jedisUtil.cache("laoli", "888");
//        jedisUtil.cache("laowang.dog", "666");
        jedisUtil.cache("18852937197", "6666");
    }
    
    //@Test
    public void testGetCacheValue() {
//        String laoli = jedisUtil.getCacheValue("laoli");
//        String laowang = jedisUtil.getCacheValue("laowang");
//        System.out.println(laoli);
//        System.out.println(laowang);
        String code = jedisUtil.getCacheValue("18852937197");
        System.out.println(code);
    }

    //@Test
    public void testCacheNxExpire() {
        long result = jedisUtil.cacheNxExpire("laotang", "666", 1000);
        System.out.println(result);
    }

    //@Test
    public void testDelKey() {
        jedisUtil.delKey("laotang");
    }

    //@Test
    public void testNoneSecurityPath() {
        parameters.getNoneSecurityPath().forEach(System.out::println);
    }

    //@Test
    public void testKafka() {
        //smsProcessor.sendSmsToKafka("test", "hello test");
        smsProcessor.sendSmsToKafka("sms.queue", "{'mobile':'18852937197','code':'6666'}");
    }

}
