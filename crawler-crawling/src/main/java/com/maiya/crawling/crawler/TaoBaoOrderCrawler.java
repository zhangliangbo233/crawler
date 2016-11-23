package com.maiya.crawling.crawler;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.maiya.common.constants.HbaseConstants;
import com.maiya.common.util.HbaseUtil;
import com.maiya.common.util.JsonUtil;
import com.maiya.common.util.MqMessageSender;
import com.maiya.crawling.dto.TaoBaoParam;
import com.maiya.crawling.webcollector.model.CrawlDatum;
import com.maiya.crawling.webcollector.model.CrawlDatums;
import com.maiya.crawling.webcollector.model.Page;
import com.maiya.crawling.webcollector.net.HttpRequest;
import com.maiya.crawling.webcollector.net.HttpResponse;
import com.maiya.crawling.webcollector.plugin.berkeley.BreadthCrawler;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.dal.model.TAOBAOSite;

@Component
public class TaoBaoOrderCrawler extends BreadthCrawler {

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TaoBaoOrderCrawler.class);

	// 关闭日志
	static {
		Logger logger = Logger.getLogger("com.gargoylesoftware.htmlunit");
		logger.setLevel(Level.OFF);
	}

	private String cookie;

	private HbaseUtil hbaseUtil;

	private String userIdentity;

	private String userChannel;

	private CrawlProxy proxy;

	private MqMessageSender mqMessageSender;

	private String taobaoMqTopic;

	private TAOBAOSite tbSite;

	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:45.0) Gecko/20100101 Firefox/45.0";
	
    public static int TIMEOUT_CONNECT = 15000;

	public TaoBaoOrderCrawler() {

	}

	public TaoBaoOrderCrawler(String crawlPath, boolean autoParse, String cookie, TAOBAOSite tbSite, HbaseUtil hbaseUtil,
			MqMessageSender mqMessageSender, String taobaoMqTopic, String userIdentity, String userChannel,
			CrawlProxy proxy) {
		super(crawlPath, autoParse);
		this.cookie = cookie;
		this.hbaseUtil = hbaseUtil;
		this.mqMessageSender = mqMessageSender;
		this.tbSite = tbSite;
		this.taobaoMqTopic = taobaoMqTopic;
		this.userIdentity = userIdentity;
		this.userChannel = userChannel;
		this.proxy = proxy;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(Page page, CrawlDatums next) {

		// 此处需要解析出所有账单的分页信息。
		if (page.meta("depth").equals("1")) {
			String html = page.getHtml();

			String data = html.substring(html.indexOf("\"extra\":"),
					html.indexOf("</script>", html.indexOf("\"extra\":")));

			// 此处主要是解析出是所有的分页加入下一次迭代中抓取
			Map<String, Object> map = (Map<String, Object>) JsonUtil.jsonToMap("{" + data);
			int totalPage = ((Map<String, Double>) map.get("page")).get("totalPage").intValue();

			for (int pageNum = 1; pageNum <= totalPage; pageNum++) {

				LOGGER.info("current depth is 1,add crawDatum");

				next.add(new CrawlDatum(
						"https://buyertrade.taobao.com/trade/itemlist/asyncBought.htm?action=itemlist/BoughtQueryAction&event_submit_do_query=1&_input_charset=utf8")
								.meta("method", "POST")
								.meta("outputData", "pageNum=" + pageNum
										+ "&pageSize=15&action=itemlist/BoughtQueryAction&prePageNo=" + (pageNum - 1))
								.setKey("" + pageNum));

			}

		}

		if (page.meta("depth").equals("2")) {

			String jsonData = page.doc().getElementsByTag("body").text();
			if (jsonData.contains("rgv587_flag0")) {
				LOGGER.warn("被淘宝反爬策略限制，当前页爬取失败");
				return;
			}

			String rowkey = this.userIdentity + "_" + this.userChannel + "_" + page.getCrawlDatum().getKey();
			LOGGER.info("rowkey:" + rowkey);
			try {
				LOGGER.info("保存订单信息到hbase开始");
				hbaseUtil.putData(HbaseConstants.HBASE_TABLE_TAOBAO, rowkey, HbaseConstants.HBASE_FAMILY,
						new String[] { HbaseConstants.HBASE_QUALIFIERS_INFOMESSAGE }, jsonData);
				LOGGER.info("保存订单信息到hbase结束");

				LOGGER.info("发送MQ消息开始");
				this.mqMessageSender.send(taobaoMqTopic, rowkey);
				LOGGER.info("发送MQ消息结束");

			} catch (IOException e) {
				LOGGER.error("保存订单信息到hbase出现异常,exception", e);
			}

		}


	}

	@Override
	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
		//
		// HttpRequest request = null;
		//
		// if (tbSite.isUseProxy()) {
		// request = new HttpRequest(crawlDatum, new Proxy(Proxy.Type.HTTP,
		// new InetSocketAddress(proxy.getProxyIp(),
		// Integer.parseInt(proxy.getProxyPort()))));
		// } else {
		// request = new HttpRequest(crawlDatum);
		// }
		//
		// request.setHeader("host", "buyertrade.taobao.com");
		// request.setHeader("Referer",
		// "https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm?spm="
		// + new Random().nextDouble());
		// request.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0)
		// Gecko/20100101 Firefox/45.0");
		// request.setCookie(cookie);
		// return request.getResponse();

		HttpRequest request = null;

		if (tbSite.isUseProxy()) {
			request = new HttpRequest(crawlDatum, new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(proxy.getProxyIp(), Integer.parseInt(proxy.getProxyPort()))));
			request.setTimeoutForConnect(TIMEOUT_CONNECT);
		} else {
			request = new HttpRequest(crawlDatum);
		}
		request.setCookie(cookie);
		request.setUserAgent(USER_AGENT);
		request.setHeader("host", new URL(tbSite.getSeedUrl()).getHost());
		@SuppressWarnings("deprecation")
		String outputData = crawlDatum.getMetaData("outputData");
		if (outputData != null) {
			request.setOutputData(outputData.getBytes("utf-8"));
			// 此处淘宝需要定制一下
			request.setHeader("Referer", "https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm?spm="
					+ new Random().nextDouble());
		}
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

	public void startCrawl(boolean autoParse, String cookie, TAOBAOSite tbSite, HbaseUtil hbaseUtil,
			MqMessageSender mqMessageSender, String taobaoMqTopic, String userIdentity, String userChannel,
			CrawlProxy proxy, TaoBaoParam taobaoParam) {

		String crawlPath = null;
		try {

			crawlPath = "taobao_crawlPath" + userIdentity + userChannel;
			TaoBaoOrderCrawler crawler = new TaoBaoOrderCrawler(crawlPath, autoParse, cookie, tbSite, hbaseUtil, mqMessageSender,
					taobaoMqTopic, userIdentity, userChannel, proxy);
			crawler.addSeed(new CrawlDatum(tbSite.getSeedUrl()).meta("method", "GET").meta("depth", "1"));
			crawler.setTopN(taobaoParam.getCrawlingOrderTopN());
			crawler.setThreads(taobaoParam.getCrawlingOrderThread());
			crawler.start(2);

		} catch (Exception ex) {
			LOGGER.error("爬取订单信息异常", ex);
		} finally {
			File file = new File(crawlPath);
			FileSystemUtils.deleteRecursively(file);
		}

	}

}
