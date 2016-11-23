package com.maiya.crawling.crawler;

import com.maiya.crawling.util.DecideByClass;
import com.maiya.dal.model.RestrictSite;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 法院失信信息抓取
 *
 * @author zhanglb
 */
@Component
public class P2PBlackCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2PBlackCrawler.class);

    /**
     * @throws Exception
     */
    public String startCrawl(RestrictSite site, WebDriver driver, String idCard, String realName) throws Exception {

        LOGGER.info("开始抓取信息 P2PBlackCrawler idCard {},realName {}", idCard, realName);
        List<WebElement> resultElements = driver.findElements(DecideByClass.getBy(site.getResultListElement(),
                site.getResultListAttr()));
        if (resultElements.isEmpty()) {
            LOGGER.info("没有查询到结果所在的元素标签,siteUrl is {},realName is {},idCard is {}", site.getSiteUrl(), realName, idCard);
            return null;
        }

        return driver.getPageSource();//页面数据
    }

}
