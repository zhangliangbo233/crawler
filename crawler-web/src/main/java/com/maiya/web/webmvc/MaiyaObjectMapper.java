package com.maiya.web.webmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

/**
 * 转换null为""
 * Created by zhanglb on 2016/9/23.
 */
public class MaiyaObjectMapper extends ObjectMapper {

    @SuppressWarnings("unchecked")
    public MaiyaObjectMapper() {
        DefaultSerializerProvider.Impl provider = new DefaultSerializerProvider.Impl();
        provider.setNullValueSerializer(new NullToEmptySerializer());
        setSerializerProvider(provider);
    }
}
