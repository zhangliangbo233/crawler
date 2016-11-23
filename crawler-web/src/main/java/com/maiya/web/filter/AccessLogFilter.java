package com.maiya.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by xiangdefei on 16/10/26.
 */
//@WebFilter(urlPatterns = "/*", filterName = "accessLogFilter")
public class AccessLogFilter implements Filter {

    private static Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);


    private static final String INVOKE_NO = "invokeNo";

    private static final String MIDDLE_LINE = "-";

    private static final String BLANK = "";

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;


        MDC.put(INVOKE_NO, UUID.randomUUID().toString().replace(MIDDLE_LINE, BLANK));

        try {
            chain.doFilter(request, response);


        } finally {

            MDC.remove(INVOKE_NO);

        }


    }

    public void init(FilterConfig config) throws ServletException {

    }

}
