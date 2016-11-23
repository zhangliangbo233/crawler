package com.maiya.dal.route;

import com.maiya.dal.route.annotation.ReadDataSource;
import com.maiya.dal.route.annotation.WriteDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * 数据库操作的dao拦截
 * 支持多个重库操作(需要修改代码)
 * Created by zhanglb on 16/11/2016.
 */
@Aspect
@Component
public class ReadWriteDataSourceAop {

    private String[] methodNames = new String[]{"select", "list", "get", "search", "query"};
    /**
     * 读写分离开关 1：开启 0：关闭
     */
    @Value("${read.write.separate.open}")
    private int readWriteSeparateOpen;

    @Pointcut("execution(public * com.*.dao.*.*(..))")
    public void daoPointCut() {
    }

    @Before("daoPointCut()")
    public void routeDataSource(JoinPoint joinPoint) {

        if (readWriteSeparateOpen != 1) {
            return;
        }
        Signature signature = joinPoint.getSignature();

        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            String methodName = methodSignature.getMethod().getName();
            for (String nameIndex : methodNames) {
                if (methodName.indexOf(nameIndex) > 0) {
                    ReadWriteDataSouceRouter.DataSouceHolder.setDataSouceName("read");
                }
            }

            //如果方法中使用注解指定使用的数据库，则覆盖如上配置
            Annotation[] annotations = methodSignature.getMethod().getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAssignableFrom(ReadDataSource.class)) {
                    ReadWriteDataSouceRouter.DataSouceHolder.setDataSouceName("read");
                }
                if (annotation.annotationType().isAssignableFrom(WriteDataSource.class)) {
                    ReadWriteDataSouceRouter.DataSouceHolder.setDataSouceName("write");
                }
            }
        }
    }
}
