package com.maiya.web.webmvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 分发请求
 * Created by zhanglb on 2016/9/29.
 */
public class MaiyaDispatcherServlet extends DispatcherServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger("PAGE-DIGEST");

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            super.doService(request, response);
        } finally {
            //记录请求日志
            StringBuilder logInfo = new StringBuilder();
            logInfo.append(request.getRequestURL()).append(",").append(request.getQueryString());

            LOGGER.info(logInfo.toString());
        }
    }
}
