package com.maiya.web.controller.restrict;

import com.maiya.crawling.service.PbccrcCrawlService;
import com.maiya.web.model.SimpleMessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 央行征信信息抓取controller
 * Created by zhanglb on 16/11/03.
 */
@RestController
public class PbccrcCrawlController {

    public static final Logger LOGGER = LoggerFactory.getLogger(PbccrcCrawlController.class);

    @Autowired
    private PbccrcCrawlService pbccrcCrawlService;

    /**
     * 抓取授信网站的信息
     *
     * @param realName 用户姓名
     * @param idCard 身份证号
     * @param userName 登录用户名
     * @param password 密码
     */
    @RequestMapping("/crawlPbccrcInfo")
    public SimpleMessageResult doCrawl(String realName, String idCard, String userName, String password) {

        SimpleMessageResult result = new SimpleMessageResult();

        pbccrcCrawlService.doCrawl(realName,idCard,userName, password);


        return result;
    }


}
