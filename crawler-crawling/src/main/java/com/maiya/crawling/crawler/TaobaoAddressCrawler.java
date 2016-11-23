package com.maiya.crawling.crawler;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.maiya.crawling.webcollector.model.CrawlDatum;
import com.maiya.crawling.webcollector.model.CrawlDatums;
import com.maiya.crawling.webcollector.model.Page;
import com.maiya.crawling.webcollector.net.EncodeHttpRequest;
import com.maiya.crawling.webcollector.net.HttpRequest;
import com.maiya.crawling.webcollector.net.HttpResponse;
import com.maiya.crawling.webcollector.plugin.berkeley.BreadthCrawler;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.dal.model.TAOBAOSite;

@Component
public class TaobaoAddressCrawler extends BreadthCrawler {

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TaobaoAddressCrawler.class);

	// 关闭日志
	static {
		Logger logger = Logger.getLogger("com.gargoylesoftware.htmlunit");
		logger.setLevel(Level.OFF);
	}

	private String cookie;

	private CrawlProxy proxy;

	private TAOBAOSite tbSite;

	private static String crawlData;

	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:45.0) Gecko/20100101 Firefox/45.0";

	public static int TIMEOUT_CONNECT = 15000;

	public TaobaoAddressCrawler() {

	}

	public TaobaoAddressCrawler(String crawlPath, String cookie, TAOBAOSite tbSite, CrawlProxy proxy) {
		super(crawlPath, true);
		this.cookie = cookie;
		this.tbSite = tbSite;
		this.proxy = proxy;

	}

	@Override
	public void visit(Page page, CrawlDatums next) {

		Document document = page.doc();
		LOGGER.debug("document"+document);
		if(document.getElementById("J_QuickLogin")!=null){
			LOGGER.warn("拦载到登录页");
			return;
		}
		Elements elements = document.getElementsByClass("tbl-deliver-address");
		if (elements.size() > 0) {
			crawlData = elements.first().html();
		}

	}

	@Override
	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {

		HttpRequest request;

		if (tbSite.isUseProxy()) {
			request = new EncodeHttpRequest(crawlDatum, new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(proxy.getProxyIp(), Integer.parseInt(proxy.getProxyPort()))));
			request.setTimeoutForConnect(TIMEOUT_CONNECT);
		} else {
			request = new EncodeHttpRequest(crawlDatum);
		}
		request.setCookie(cookie);
		request.setUserAgent(USER_AGENT);
		request.setHeader("host", new URL(tbSite.getAddressSeedUrl()).getHost());
		request.setHeader("accept-language", "zh-CN,zh;q=0.8,en;q=0.6");
		request.setHeader("Referer", "https://i.taobao.com/my_taobao.htm");
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		return request.getResponse();

	}

	public String startCrawl(String cookie, TAOBAOSite tbSite, String userIdentity, String userChannel,
			CrawlProxy proxy) {
		String crawlPath = null;
		try {
			crawlPath = "taobao_crawlPath_address" + userIdentity + userChannel;
			TaobaoAddressCrawler crawler = new TaobaoAddressCrawler(crawlPath, cookie, tbSite, proxy);
			crawler.addSeed(new CrawlDatum(tbSite.getAddressSeedUrl()));
			crawler.start(1);
		} catch (Exception ex) {
			LOGGER.error("爬取收货地址信息异常", ex);
		} finally {
			File file = new File(crawlPath);
			FileSystemUtils.deleteRecursively(file);
		}

		return crawlData;
	}

}
