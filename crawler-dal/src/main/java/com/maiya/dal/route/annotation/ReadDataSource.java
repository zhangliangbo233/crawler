package com.maiya.dal.route.annotation;

import java.lang.annotation.*;

/**
 * 读数据库
 * Created by zhanglb on 16/11/2016.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ReadDataSource {
}
