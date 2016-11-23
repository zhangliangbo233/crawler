package com.maiya.web.controller.restrict;

import com.maiya.common.enums.ReturnCodeEnum;
import com.maiya.common.exceptions.CustomBusinessException;
import com.maiya.crawling.service.RestrictCrawlService;
import com.maiya.web.model.SimpleMessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 授信网站信息抓取controller
 * Created by zhanglb on 16/8/26.
 */
@RestController
public class RestrictCrawlController {

    public static final Logger LOGGER = LoggerFactory.getLogger(RestrictCrawlController.class);

    @Autowired
    private RestrictCrawlService restrictCrawService;

    /**
     * 抓取授信网站的信息
     *
     * @param realName 用户姓名
     * @param idCard   身份证号
     * @param siteType 指定查询的网站
     */
    @RequestMapping("/crawlRestrictInfo")
    public SimpleMessageResult doCrawl(String realName, String idCard, Long siteType) {

        SimpleMessageResult result = new SimpleMessageResult();

        try {
            //先返回结果,在继续执行爬取任务
            restrictCrawService.doCrawl(realName, idCard, siteType, null);
            result.setCode(ReturnCodeEnum.SUCCESS.getCode());
        } catch (CustomBusinessException e) {
            LOGGER.error("抓取信息发生异常", e);
            result.setCode(ReturnCodeEnum.INVALID_PARAMETERS.getCode());
            result.setMessage(e.getMessage());
        } catch (UnsupportedEncodingException | InterruptedException e) {
            LOGGER.error("抓取信息发生异常", e);
            result.setCode(ReturnCodeEnum.FAIL.getCode());
            result.setMessage(ReturnCodeEnum.FAIL.getMessage());
        }
        return result;
    }

    /**
     * 查询用户是否在授信网站有不良信息
     *
     * @param realName 用户姓名
     * @param idCard   身份证号
     * @param siteType 指定查询的网站
     */
    @RequestMapping("/obtainUserCreditInfo")
    public SimpleMessageResult obtainUserCreditInfo(String realName, String idCard, Long siteType) {

        SimpleMessageResult result = new SimpleMessageResult();
        try {
            result.setCode(ReturnCodeEnum.SUCCESS.getCode());
            result.setData(restrictCrawService.obtainUserCreditInfo(realName, idCard, siteType));
        } catch (CustomBusinessException e) {
            LOGGER.error("获取用户授信信息发生异常", e);
            result.setCode(ReturnCodeEnum.INVALID_PARAMETERS.getCode());
            result.setMessage(e.getMessage());
        } catch (IOException e) {
            LOGGER.error("获取用户授信信息发生异常", e);
            result.setCode(ReturnCodeEnum.FAIL.getCode());
            result.setMessage(ReturnCodeEnum.FAIL.getMessage());
        }
        return result;
    }


}
