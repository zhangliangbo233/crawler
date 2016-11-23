package com.maiya.crawling.crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import com.maiya.crawling.util.RestrictSiteCookie;
import com.maiya.dal.model.RestrictSite;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.net.URLEncoder;

/**
 * 央行征信中心信息抓取
 *
 * @author zhanglb
 */
@Component
public class PbccrcCrawler extends BreadthCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PbccrcCrawler.class);

    private String cookie;

    private static String pageData;//网页数据

    private boolean autoParse;

    public PbccrcCrawler() {
        super("pbccrc_crawler", false);
    }

    public PbccrcCrawler(String crawlPath, boolean autoParse, String cookie) {
        super(crawlPath, autoParse);
        this.autoParse = autoParse;
        this.cookie = cookie;
        addRegex("-.*\\.(jpg|png|gif).*");//不要爬取 jpg|png|gif
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setCookie(cookie);
        return request.getResponse();
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        Element element = page.select("div[id=result1]", 0);
        pageData = element.outerHtml();
    }

    @Override
    protected void afterParse(Page page, CrawlDatums next) {
    }

    /**
     * @return
     * @throws Exception
     */
    public String startCrawl(RestrictSite site, WebDriver driver, String idCard, String realName) throws Exception {
        LOGGER.info("开始抓取信息 PbccrcCrawler idCard {},realName {}",idCard,realName);

        try {
            StringBuilder keyWords = new StringBuilder();
            keyWords.append(realName).append(" ").append(idCard);
            String cookie = RestrictSiteCookie.concatCookie(driver);
            PbccrcCrawler crawler = new PbccrcCrawler("pbccrc_crawler_"+idCard, autoParse, cookie);

            String seedUrl = createSearchUrl(site.getSiteDataUrl(), keyWords.toString(), 1);
            crawler.addSeed(seedUrl, false);
            crawler.setThreads(3);
            crawler.start(1);
        } finally {
            File file = new File("pbccrc_crawler_"+idCard);
            FileSystemUtils.deleteRecursively(file);
        }

        return pageData;
    }

    /**
     * 根据关键词和页号拼接搜索对应的URL
     *
     * @param crawlPath
     * @param keyword   查询的关键字
     * @param pageNum   分页
     */
    private String createSearchUrl(String crawlPath, String keyword, int pageNum) throws Exception {
        LOGGER.info("抓取的url is {},搜索关键字 is {}", crawlPath, keyword);
        int first = pageNum * 10 - 9;
        keyword = URLEncoder.encode(keyword, "UTF-8");
        return String.format(crawlPath, keyword, first);
    }

}
