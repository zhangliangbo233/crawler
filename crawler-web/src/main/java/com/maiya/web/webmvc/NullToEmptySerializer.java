package com.maiya.web.webmvc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * null 转为 ""
 * Created by zhanglb on 2016/9/23.
 */
public class NullToEmptySerializer extends JsonSerializer {
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeString("");
    }
}
