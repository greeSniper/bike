package com.tangzhe.bike.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * Created by tangzhe 2017/9/11.
 * 消息发送者
 */
@Component("smsProcessor")
public class SmsProcessor {

    @Autowired
    private JmsMessagingTemplate jmsTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 生产消息到activeMq
     */
    public void sendSmsToQueue(Destination destination, final String message){
        jmsTemplate.convertAndSend(destination, message);
    }

    /**
     * 生产消息到kafka
     */
    public void sendSmsToKafka(String destination, final String message){
        kafkaTemplate.send(destination, message);
    }

}
