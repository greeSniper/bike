package com.tangzhe.bike.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by 唐哲
 * 2018-06-05 18:09
 * 消息接收者
 */
@Component("smsConsumer")
public class SmsConsumer {

    @Autowired
    @Qualifier("miaoDiSmsSender")
    private SmsSender smsSender;

    /**
     * 从activeMq消费消息，将消费的消息通过秒嘀发送短信至用户
     */
    @JmsListener(destination="sms.queue")
    public void doSendSmsMessage(String text){
        JSONObject jsonObject = JSON.parseObject(text);
        smsSender.sendSms(jsonObject.getString("mobile"), jsonObject.getString("tplId"), jsonObject.getString("vercode"));
    }

    /**
     * 从kafka消费消息，将消费的消息通过秒嘀发送短信至用户
     */
    @KafkaListener(topics = {"sms.queue"})
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            String text = kafkaMessage.get().toString();
            JSONObject jsonObject = JSON.parseObject(text);
            smsSender.sendSms(jsonObject.getString("mobile"), jsonObject.getString("tplId"), jsonObject.getString("vercode"));
        }

    }

}
