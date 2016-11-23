package com.maiya.crawling.crawler;

import com.alibaba.fastjson.JSON;
import com.maiya.common.constants.MaiyaCrawlConstants;
import com.maiya.common.enums.ReturnCodeEnum;
import com.maiya.common.exceptions.CustomBusinessException;
import com.maiya.crawling.util.CaptchaConverter;
import com.maiya.crawling.util.DecideByClass;
import com.maiya.crawling.util.RestrictSiteCookie;
import com.maiya.dal.model.RestrictSite;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.maiya.crawling.util.CaptchaConverter.*;

/**
 * 法院失信信息抓取
 *
 * @author zhanglb
 */
@Component
public class ShiXinCourtCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiXinCourtCrawler.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${firefox.user.agent}")
    private String firefoxAgent;

    /**
     * @throws Exception
     */
    @SuppressWarnings("all")
    public String startCrawl(RestrictSite site, WebDriver driver, String idCard, String realName) throws Exception {

        LOGGER.info("开始抓取信息 ShiXinCourtCrawler idCard {},realName {}", idCard, realName);

        //获取[验证码错误，请重新输入！]的情况，出现此情况的时候可以立即重试
        shixinReSendCode(site, driver, idCard, realName, 0);

        //获取[查看]元素
        List<WebElement> detailElements = driver.findElements(DecideByClass.getBy("View", "class"));
        if (detailElements.isEmpty()) {
            LOGGER.info("没有查询到结果所在的元素标签,siteUrl is {},realName is {},idCard is {}", site.getSiteUrl(), realName, idCard);
            return null;
        }

        List<Map<String, String>> data = new ArrayList<>();

        //设置header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", RestrictSiteCookie.concatCookie(driver));
        requestHeaders.add("Host", new URL(site.getSiteUrl()).getHost());
        requestHeaders.add("User-Agent", firefoxAgent);
        requestHeaders.add("Referer", "http://shixin.court.gov.cn/");
        requestHeaders.add("X-Requested-With", "XMLHttpRequest");
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);

        for (WebElement aElement : detailElements) {
            //因为此网站的验证码每隔30秒刷新一次,为了保证验证码的准确性,需要实时保存验证码
            driver.switchTo().defaultContent();
            String pCodeImgUrl = driver.findElement(By.id(site.getCodeImgElement())).getAttribute("src");
            saveCodeImage(pCodeImgUrl, idCard, site.getId(), driver);
            String pCode = parseCodeImg(idCard, site.getId());

            driver.switchTo().frame(site.getContentFrameElement());
            if (StringUtils.isNotEmpty(site.getCodeImgElement())) {//验证码图片
                String rowId = aElement.getAttribute("id");
                String crawlUrl = createCrawlUrl(site.getSiteDataUrl(), rowId, pCode);

                Map<String, String> aMap = null;
                try {
                    ResponseEntity<String> response = restTemplate.exchange(crawlUrl,
                            HttpMethod.GET, requestEntity, String.class);

                    aMap = JSON.parseObject(response.getBody(), Map.class);
                    if (aMap.isEmpty()) {
                        LOGGER.info("失信网站解析一行结果为空,responseBody is {} ", response.getBody());
                        continue;
                    }
                } catch (RestClientException e) {
                    LOGGER.error("抓取失信接口出现错误,{}", e);
                    continue;
                }
                data.add(aMap);
            }
        }

        return data.isEmpty() ? null : JSON.toJSONString(data);
    }

    /**
     * 根据每一行id和验证码拼接对应的URL
     *
     * @param crawlPath
     * @param rowId     每一行的id
     * @param pCode     验证码
     */
    private String createCrawlUrl(String crawlPath, String rowId, String pCode) {
        return String.format(crawlPath, rowId, pCode);
    }

    /**
     * 获取[验证码错误，请重新输入！]的情况，出现此情况的时候可以立即重试
     * 出现此情况立即重新获取新的验证码重试
     *
     * @return
     */
    private void shixinReSendCode(RestrictSite site, WebDriver driver, String idCard, String realName, int reSendNum) throws Exception {
        reSendNum++;
        if (reSendNum > MaiyaCrawlConstants.DEFAULT_CALL_COURT_COUNT) {
            LOGGER.info("ZhiXingCourtCrawler 出现验证码错误,已经重试{}次,已经大于默认的验证码错误的重试次数", reSendNum - 1);
            throw new CustomBusinessException(ReturnCodeEnum.INVALID_PARAMETERS,
                    "身份证" + idCard + ",姓名" + realName + ",失信网站输入" + (reSendNum - 1) + "验证码都失败");
        }
        driver.switchTo().frame(site.getContentFrameElement());
        Thread.sleep(CaptchaConverter.randomInt());
        WebElement resultListBlock = driver.findElement(DecideByClass.getBy("ResultlistBlock", "id"));
        if (resultListBlock == null) {
            LOGGER.info("没有查询到结果的block,名称为resultListBlock");
            return;
        }

        if (resultListBlock.getText().contains("验证码错误")) {

            LOGGER.info("ShiXinCourtCrawler 出现验证码错误,第{}次填写验证码", reSendNum);

            if (StringUtils.isNotEmpty(site.getCodeElement())) {//验证码输入框
                driver.switchTo().defaultContent();
                Thread.sleep(randomInt());
                String pCodeImgUrl = driver.findElement(By.id(site.getCodeImgElement())).getAttribute("src");
                saveCodeImage(pCodeImgUrl, idCard, site.getId(), driver);
                driver.findElement(By.id(site.getCodeElement())).clear();
                driver.findElement(By.id(site.getCodeElement())).sendKeys(parseCodeImg(idCard, site.getId()));
                driver.findElement(DecideByClass.getBy(site.getSearchButtonElement(),
                        site.getSearchButtonAttr())).click();//提交按钮
            }
            shixinReSendCode(site, driver, idCard, realName, reSendNum);
        }
    }

}
