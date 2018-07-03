package com.tangzhe.bike.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tangzhe.bike.common.constants.Constants;
import com.tangzhe.bike.common.exception.BikeException;
import com.tangzhe.bike.common.util.QiniuFileUploadUtil;
import com.tangzhe.bike.common.util.RandomNumberCode;
import com.tangzhe.bike.common.util.cache.JedisUtil;
import com.tangzhe.bike.common.util.security.AESUtil;
import com.tangzhe.bike.common.util.security.Base64Util;
import com.tangzhe.bike.common.util.security.MD5Util;
import com.tangzhe.bike.common.util.security.RSAUtil;
import com.tangzhe.bike.entity.User;
import com.tangzhe.bike.entity.UserElement;
import com.tangzhe.bike.repository.UserRepository;
import com.tangzhe.bike.sms.SmsProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.Destination;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by 唐哲
 * 2018-06-02 10:41
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    // 存在redis中验证次数的key
    private static final String VERIFYCODE_PREFIX = "verify.code.";

    // 发送短信验证码消息队列名称
    private static final String SMS_QUEUE ="sms.queue";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private SmsProcessor smsProcessor;

    @Override
    public List<User> findAll() throws BikeException {
        List<User> userList = null;
        try {
            userList = userRepository.findAll();
        } catch (Exception e) {
            log.error("Failed to find all user", e);
            throw new BikeException("查询所有用户失败");
        }
        return userList;
    }

    @Override
    public String login(String data, String key) {
        try {
            // RSA解密AES的密钥
            byte[] keyBytes = RSAUtil.getInstance().decryptByPrivateKey(Base64Util.getInstance().decode(key));
            // AES通过密钥解密data
            String decryptData = AESUtil.getInstance().decrypt(data, new String(keyBytes, "UTF-8"));
            // 校验数据
            if (decryptData == null) {
                throw new Exception();
            }

            // 获取手机号及验证码
            JSONObject jsonObject = JSON.parseObject(decryptData);
            String mobile = jsonObject.getString("mobile");
            String code = jsonObject.getString("code");
            String platform = jsonObject.getString("platform");
            if(StringUtils.isBlank(mobile) || StringUtils.isBlank(code)) {
                throw new Exception();
            }

            // 去redis取验证码 比较手机号码和验证码是不是匹配 若匹配则说明是本人手机
            String verCode = jedisUtil.getCacheValue(mobile);
            // 判断安卓端传过来的验证码与redis中验证码是否相同
            if (!code.equals(verCode)) {
                throw new BikeException("手机号验证码不匹配");
            }

            User user;
            // 手机验证码匹配，通过手机号查询用户
            user = userRepository.findUserByMobile(mobile);
            // 用户不存在，则帮用户注册
            if(user == null) {
                user = new User();
                user.setMobile(mobile);
                user.setNickname(mobile);
                userRepository.save(user);
            }

            // 生成token
            String token;
            try {
                token = generateToken(user);
            } catch (Exception e) {
                throw new BikeException("生成token失败");
            }

            // 将token存入redis中，key为token，value为用户信息
            UserElement ue = new UserElement(user.getId(), user.getMobile(), token, platform);
            jedisUtil.putTokenWhenLogin(ue);

            return token;
        } catch (Exception e) {
            log.error("Fail to decrypt data", e);
            throw new BikeException(e.getMessage() + "解析数据失败");
        }
    }

    @Override
    public void modifyNickName(User user) {
        userRepository.updateNickName(user.getId(), user.getNickname());
    }

    @Override
    public void sendVercode(String mobile, String ip) {
        try {
            // 生成验证码
            String verCode = RandomNumberCode.getInstance().verCoder();

            // 校验
            int result = jedisUtil.cacheForVerificationCode(VERIFYCODE_PREFIX + mobile, verCode, "reg", 60, ip);;
            if (result == 1) {
                log.info("当前验证码未过期，请稍后重试");
                throw new BikeException("当前验证码未过期，请稍后重试");
            } else if (result == 2) {
                log.info("超过当日验证码次数上限");
                throw new BikeException("超过当日验证码次数上限");
            } else if (result == 3) {
                log.info("{}超过当日请求验证码次数上限", ip);
                throw new BikeException(ip + "超过当日请求验证码次数上限");
            }

            // 记录日志
            log.info("Sending verify code {} for phone {}", verCode, mobile);

            //校验通过 发送短信 发消息到队列
            Destination destination = new ActiveMQQueue(SMS_QUEUE);
            Map<String, String> smsParam = new HashMap<>();
            smsParam.put("mobile", mobile);
            smsParam.put("tplId", Constants.MDSMS_VERCODE_TPLID);
            smsParam.put("vercode", verCode);
            String message = JSON.toJSONString(smsParam);

            // 发送消息队列给另外一个服务，由另外那个服务发送手机短信验证码(模拟)
            smsProcessor.sendSmsToQueue(destination, message); // 使用activeMq
            //smsProcessor.sendSmsToKafka(SMS_QUEUE, message); // 使用kafka
        } catch (Exception e) {
            log.error("Fail to send verCode", e);
            throw new BikeException("发送短信失败");
        }
    }

    @Override
    public String uploadHeadImg(MultipartFile file, long userId) {
        try {
            // 获取user 得到原来的头像地址
            User user = userRepository.findById(userId).get();
            // 调用七牛
            String imgUrlName = QiniuFileUploadUtil.getInstance().uploadHeadImg(file);
            user.setHeadImg(imgUrlName);
            // 更新用户头像URL
            userRepository.save(user);
            return Constants.QINIU_HEAD_IMG_BUCKET_URL + "/"+Constants.QINIU_HEAD_IMG_BUCKET_NAME+"/"+imgUrlName;
        } catch (Exception e) {
            log.error("Fail to upload head img", e);
            throw new BikeException("头像上传失败");
        }
    }

    /**
     * 生成token
     */
    private String generateToken(User user) {
        String source = user.getId() + ":" + user.getMobile() + ":" + System.currentTimeMillis();
        return MD5Util.getInstance().getMD5(source);
    }

}
