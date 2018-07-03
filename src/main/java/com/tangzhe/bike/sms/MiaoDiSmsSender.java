package com.tangzhe.bike.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tangzhe.bike.common.constants.Constants;
import com.tangzhe.bike.common.util.HttpUtil;
import com.tangzhe.bike.common.util.security.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangzhe 2017/9/11.
 */
@Service("miaoDiSmsSender")
public class MiaoDiSmsSender implements SmsSender{

    private Logger log = LoggerFactory.getLogger(MiaoDiSmsSender.class);

    //demo上直接有的
    private static String operation = "/industrySMS/sendSMS";

    /**
     * 秒滴发送短信
     */
    @Override
    public  void sendSms(String phone, String tplId, String params){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = sdf.format(new Date());
            String sig = MD5Util.getInstance().getMD5(Constants.MDSMS_ACCOUNT_SID + Constants.MDSMS_AUTH_TOKEN +timestamp);
            String url = Constants.MDSMS_REST_URL + operation;
            Map<String,String> param = new HashMap<>();
            param.put("accountSid", Constants.MDSMS_ACCOUNT_SID); // sid
            param.put("to", phone); // 接收者电话
            param.put("templateid", tplId); // 模板id
            param.put("param", params); // 参数
            param.put("timestamp", timestamp); // 日期
            param.put("sig", sig); // accountSid和authToken经过md5加密
            param.put("respDataType","json");
            String result = HttpUtil.getInstance().post(url, param);
            JSONObject jsonObject = JSON.parseObject(result);
            if(!jsonObject.getString("respCode").equals("00000")){
                log.error("fail to send sms to " + phone + ": " + params + ":" + result);
            }
        } catch (Exception e) {
            log.error("fail to send sms to " + phone + ":" + params);
        }
    }

}
