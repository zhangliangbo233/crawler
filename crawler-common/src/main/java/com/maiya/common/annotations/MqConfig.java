package com.maiya.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mq连接的配置信息
 * Created by zhanglb on 16/9/1.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqConfig {

    /**
     * MQ连接工厂
     *
     * @return
     */
    String connectionFactory() default "connectionFactory";

    /**
     * MQ destination对象名
     *
     * @return
     */
    String destination();

    /**
     * destination的队列名
     * @return
     */
    String destinationPhysicalName();



    /**
     * 是否开启事务
     *
     * @return
     */
    boolean sessionTransacted() default false;

    /**
     * 并发消费者个数
     *
     * @return
     */
    int concurrentConsumers() default 1;

}
