package com.maiya.crawling.util;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.Set;

/**
 * 拼接cookie串
 * Created by zhanglb on 16/9/12.
 */

public class RestrictSiteCookie {

    public static String concatCookie(WebDriver webDriver) throws Exception {

        Set<Cookie> cookies = webDriver.manage().getCookies();

        StringBuilder cookieBuff = new StringBuilder();
        // 拼接cookie
        for (Cookie cookie : cookies) {
            cookieBuff.append(cookie.getName() + "=" + cookie.getValue() + ";");
        }
        return cookieBuff.toString();
    }
}
