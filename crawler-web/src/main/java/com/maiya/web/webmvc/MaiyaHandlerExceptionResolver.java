package com.maiya.web.webmvc;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.maiya.common.enums.ReturnCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统默认异常处理
 * Created by zhanglb on 16/9/23.
 */
public class MaiyaHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaiyaHandlerExceptionResolver.class);


    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                                              Object handler, Exception e) {
        FastJsonJsonView jsonView = new FastJsonJsonView();
        ModelAndView mv = new ModelAndView(jsonView);
        Map<String, String> attributes = new HashMap<String, String>();
        if (e instanceof MissingServletRequestParameterException) {
            LOGGER.error(e.getMessage());
            attributes.put("code", ReturnCodeEnum.INVALID_PARAMETERS.getCode());
            attributes.put("message", ReturnCodeEnum.INVALID_PARAMETERS.getMessage());
        } else {
            LOGGER.error(e.getMessage(), e);
            attributes.put("code", ReturnCodeEnum.FAIL.getCode());
            attributes.put("message", ReturnCodeEnum.FAIL.getMessage());
        }
        jsonView.setAttributesMap(attributes);

        return mv;
    }

}