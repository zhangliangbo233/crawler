package com.maiya.crawling.webcollector.crawler;

import com.maiya.crawling.webcollector.model.CrawlDatum;
import com.maiya.crawling.webcollector.model.Links;
import com.maiya.crawling.webcollector.model.Page;
import com.maiya.crawling.webcollector.net.Requester;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiya.crawling.webcollector.fetcher.Executor;
import com.maiya.crawling.webcollector.fetcher.Visitor;
import com.maiya.crawling.webcollector.model.CrawlDatums;
import com.maiya.crawling.webcollector.net.HttpRequest;
import com.maiya.crawling.webcollector.net.HttpResponse;
import com.maiya.crawling.webcollector.util.RegexRule;



public abstract class AutoParseCrawler extends Crawler implements Executor, Visitor, Requester {

    public static final Logger LOG = LoggerFactory.getLogger(AutoParseCrawler.class);

    /**
     * 是否自动抽取符合正则的链接并加入后续任务
     */
    protected boolean autoParse = true;

    protected Visitor visitor;
    protected Requester requester;
    
    public AutoParseCrawler(){
    	
    }

    public AutoParseCrawler(boolean autoParse) {
        this.autoParse = autoParse;
        this.visitor = this;
        this.requester = this;
        this.executor = this;
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        return request.getResponse();
    }

    /**
     * URL正则约束
     */
    protected RegexRule regexRule = new RegexRule();

    @Override
    public void execute(CrawlDatum datum, CrawlDatums next) throws Exception {
        HttpResponse response = requester.getResponse(datum);
        Page page = new Page(datum, response);
        visitor.visit(page, next);
        if (autoParse && !regexRule.isEmpty()) {
            parseLink(page, next);
        }
        afterParse(page, next);
    }

    protected void afterParse(Page page, CrawlDatums next) {

    }

    protected void parseLink(Page page, CrawlDatums next) {
        String conteType = page.getResponse().getContentType();
        if (conteType != null && conteType.contains("text/html")) {
            @SuppressWarnings("deprecation")
			Document doc = page.getDoc();
            if (doc != null) {
                Links links = new Links().addByRegex(doc, regexRule);
                next.add(links);
            }
        }

    }

    /**
     * 添加URL正则约束
     *
     * @param urlRegex URL正则约束
     */
    public void addRegex(String urlRegex) {
        regexRule.addRule(urlRegex);
    }

    /**
     *
     * @return 返回是否自动抽取符合正则的链接并加入后续任务
     */
    public boolean isAutoParse() {
        return autoParse;
    }

    /**
     * 设置是否自动抽取符合正则的链接并加入后续任务
     *
     * @param autoParse 是否自动抽取符合正则的链接并加入后续任务
     */
    public void setAutoParse(boolean autoParse) {
        this.autoParse = autoParse;
    }

    /**
     * 获取正则规则
     *
     * @return 正则规则
     */
    public RegexRule getRegexRule() {
        return regexRule;
    }

    /**
     * 设置正则规则
     *
     * @param regexRule 正则规则
     */
    public void setRegexRule(RegexRule regexRule) {
        this.regexRule = regexRule;
    }

    /**
     * 获取Visitor
     *
     * @return Visitor
     */
    public Visitor getVisitor() {
        return visitor;
    }

    /**
     * 设置Visitor
     *
     * @param visitor Visitor
     */
    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Requester getRequester() {
        return requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }
}
