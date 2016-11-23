package com.maiya.crawling.crawler;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.maiya.common.constants.HbaseConstants;
import com.maiya.common.util.HbaseUtil;
import com.maiya.common.util.JsonUtil;
import com.maiya.crawling.dto.JDParam;
import com.maiya.crawling.webcollector.model.CrawlDatum;
import com.maiya.crawling.webcollector.model.CrawlDatums;
import com.maiya.crawling.webcollector.model.Page;
import com.maiya.crawling.webcollector.net.HttpRequest;
import com.maiya.crawling.webcollector.net.HttpResponse;
import com.maiya.crawling.webcollector.net.Proxys;
import com.maiya.crawling.webcollector.plugin.berkeley.BreadthCrawler;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.dal.model.JDSite;

@Component
public class JDOrderCrawler extends BreadthCrawler {

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JDOrderCrawler.class);

	// 关闭日志
	static {
		Logger logger = Logger.getLogger("com.gargoylesoftware.htmlunit");
		logger.setLevel(Level.OFF);
	}

	private String cookie;

	private String userIdentity;

	private String userChannel;

	private HbaseUtil hbaseUtil;

	private CrawlProxy proxy;

	private String orderJdHost;

	private JDSite jdSite;

	private Proxys proxys = new Proxys();

	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:45.0) Gecko/20100101 Firefox/45.0";
	
	public static int TIMEOUT_CONNECT = 20000;

	public JDOrderCrawler() {

	}

	public JDOrderCrawler(String crawlPath, boolean autoParse, String cookie, String orderJdHost, JDSite jdSite,
			String userIdentity, String userChannel, HbaseUtil hbaseUtil, CrawlProxy proxy) throws Exception {
		super(crawlPath, autoParse);
		this.cookie = cookie;
		this.orderJdHost = orderJdHost;
		this.jdSite = jdSite;
		this.userIdentity = userIdentity;
		this.userChannel = userChannel;
		this.hbaseUtil = hbaseUtil;
		this.proxy = proxy;
	}

	@Override
	public void visit(Page page, CrawlDatums next) {

		// depth为1,获取总页数,设置爬取URL
		if (page.meta("depth").equals("1")) {

			// Element element = page.select("div[class=pagin
			// fr]").select("a").last().previousElementSibling();
			//
			// int totalPage = Integer.parseInt(element.text());

			int totalPage = 30;

			for (int i = 1; i <= totalPage; i++) {
				next.add(new CrawlDatum("https://order.jd.com/center/list.action?search=0&d=2&s=4096&page=" + i)
						.meta("method", "GET").setKey("" + i));
			}
		}

		if (page.meta("depth").equals("2")) {

			String dataJson = ExtractOrderData(page);

			LOGGER.info("dataJson:" + dataJson);

			if (StringUtils.isNoneBlank(dataJson)) {

				String rowkey = this.userIdentity + "_" + this.userChannel + "_" + page.getCrawlDatum().getKey();
				LOGGER.info("rowkey:" + rowkey);
				try {
					LOGGER.info("存储数据到hbase start");
					hbaseUtil.putData(HbaseConstants.HBASE_TABLE_JD, rowkey, HbaseConstants.HBASE_FAMILY,
							new String[] { HbaseConstants.HBASE_QUALIFIERS_ANALYSISMESSAGE }, dataJson);
					LOGGER.info("存储数据到hbase end");

				} catch (IOException e) {

					LOGGER.error("put data to hbase exception", e);
				}
			}

		}

	}

	/**
	 * 抽取订单数据
	 * 
	 * @param page
	 * @return
	 */
	private String ExtractOrderData(Page page) {

		try {

			Elements elements = page.select("tbody[id^=tb]");
			if (elements.size() > 0) {
				Map<String, String> map = null;
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				for (Element element : elements) {
					map = new HashMap<String, String>();
					String pageData = element.html();
					Document doc = Jsoup.parse(pageData);
					map.put("dealtime", doc.getElementsByClass("dealtime").text());
					map.put("number", doc.getElementsByClass("number").text());
					map.put("amount", doc.getElementsByClass("amount").text());
					map.put("orderStatus", doc.getElementsByClass("order-status").text());
					list.add(map);
				}
				String dataJson = JsonUtil.objectToJson(list);
				return dataJson;
			}
		} catch (Exception ex) {

			LOGGER.error("解析数据出现异常,exception:{}", ex);
		}
		return null;
	}

	@Override
	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
		HttpRequest request = null;
		if (jdSite.isUseProxy()) {
			request = new HttpRequest(crawlDatum, new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(proxy.getProxyIp(), Integer.parseInt(proxy.getProxyPort()))));
			request.setTimeoutForConnect(TIMEOUT_CONNECT);
		} else {
			request = new HttpRequest(crawlDatum);
		}

		request.setCookie(cookie);
		request.setHeader("Host", orderJdHost);
		request.setHeader("User-Agent", USER_AGENT);
		return request.getResponse();
	}

	// parse完后需要设置当前的深度，以供具体的需求
	@Override
	protected void afterParse(Page page, CrawlDatums next) {
		// 当前页面的depth为x，则从当前页面解析的后续任务的depth为x+1
		int depth;
		// 如果在添加种子时忘记添加depth信息，可以通过这种方式保证程序不出错
		if (page.meta("depth") == null) {
			depth = 1;
		} else {
			depth = Integer.valueOf(page.meta("depth"));
		}
		depth++;
		for (CrawlDatum datum : next) {
			datum.meta("depth", depth + "");
		}
	}

	public void startCraw(boolean autoParse, String cookie, JDParam jdParam, JDSite jdSite, String userIdentity,
			String userChannel, HbaseUtil hbaseUtil, CrawlProxy proxy) {

		String crawlPath = null;
		try {
			crawlPath = "jd_crawlPath" + userIdentity + userChannel;
			JDOrderCrawler crawler = new JDOrderCrawler(crawlPath, autoParse, cookie, jdParam.getOrderJdHost(), jdSite,
					userIdentity, userChannel, hbaseUtil, proxy);
			crawler.addSeed(new CrawlDatum(jdSite.getSeedUrl()).meta("method", "GET").meta("depth", "1"));
			crawler.setThreads(jdParam.getCrawlingOrderThread());
			crawler.setTopN(jdParam.getCrawlingOrderTopN());
			crawler.start(2);

		} catch (Exception e) {
			LOGGER.error("爬取异常", e);
		} finally {
			File file = new File(crawlPath);
			FileSystemUtils.deleteRecursively(file);
		}

	}

}
