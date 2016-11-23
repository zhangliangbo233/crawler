package com.maiya.common.util;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * mq 消息发送
 * Created by zhanglb on 16/9/1.
 */
public class MqMessageSender extends JmsTemplate{

    /**
     * 发送消息
     * @param message
     */
    public void send(String destinationName,final String message){
        super.send(destinationName,new MessageCreator(){
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
    }

}
