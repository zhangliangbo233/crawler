package com.maiya.common.util;

import com.maiya.common.annotations.MqConfig;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageListener;
import java.util.HashSet;
import java.util.Set;

/**
 * 注册MqConfig
 * Created by zhanglb on 16/9/1.
 */
public class MqConfigAnnotationRegister implements BeanPostProcessor, ApplicationListener {

    /**
     * 消费者
     */
    private Set<DefaultMessageListenerContainer> containers = new HashSet<DefaultMessageListenerContainer>();

    private boolean started;

    private boolean stopped;

    /**
     * 注册监听
     *
     * @param bean
     */
    private void registContainers(Object bean) {
        Class<? extends Object> className = bean.getClass();
        if (!className.isAnnotationPresent(MqConfig.class)) {
            return;
        }
        if (!MessageListener.class.isAssignableFrom(className)) {
            return;
        }

        MqConfig mqConfig = className.getAnnotation(MqConfig.class);
        String connectionFactory = mqConfig.connectionFactory();
        String destinationName = mqConfig.destination();
        String physicalName = mqConfig.destinationPhysicalName();
        int concurrentConsumers = mqConfig.concurrentConsumers();
        boolean sessionTransacted = mqConfig.sessionTransacted();

        MessageListener msgListener = (MessageListener) bean;
        ConnectionFactory connFactory = SpringContextHolder.getBean(connectionFactory);
        Destination destination = SpringContextHolder.getBean(destinationName);
        ActiveMQQueue activeMQQueue = null;
        if (destination instanceof ActiveMQQueue){
            activeMQQueue = (ActiveMQQueue) destination;
            activeMQQueue.setPhysicalName(physicalName);
        }

        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connFactory);
        if (activeMQQueue != null){
            container.setDestination(activeMQQueue);
        }else {
            container.setDestination(destination);
        }
        container.setMessageListener(msgListener);
        container.setSessionTransacted(sessionTransacted);
        container.setConcurrentConsumers(concurrentConsumers);

        containers.add(container);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        registContainers(bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 监听applicationContext的状态,开启或关闭mq的消费者监听
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent
                && ((ContextRefreshedEvent) event).getApplicationContext() == SpringContextHolder.getApplicationContext()) {
            if (started) {
                return;
            }
            started = true;
            for (DefaultMessageListenerContainer container : containers) {
                container.afterPropertiesSet();
                container.start();
            }
        } else if (event instanceof ContextClosedEvent
                && ((ContextClosedEvent) event).getApplicationContext() == SpringContextHolder.getApplicationContext()) {
            if (stopped) {
                return;
            }

            stopped = true;
            for (DefaultMessageListenerContainer container : containers) {
                container.stop();
                container.shutdown();
            }
        }
    }


}
