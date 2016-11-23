package com.maiya.crawling.crawler;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.maiya.crawling.service.CrawlB2CRetryTaskService;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.dal.model.TAOBAOSite;

/**
 * 淘宝收获地址爬取
 * 
 * @author xiangdf
 *
 */
@Component
public class TaoBaoAddressInfoCrawler {

	public static final Logger LOGGER = LoggerFactory.getLogger(TaoBaoAddressInfoCrawler.class);

	@Value("${firefox.user.agent}")
	private String userAgent;

	@Autowired
	private RestTemplate restTemplate;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String startCrawl(String cookie, TAOBAOSite tbSite, CrawlProxy proxy) throws MalformedURLException {

		LOGGER.info("开始爬取淘宝收货地址信息");
		// 设置header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Cookie", cookie);
		requestHeaders.add("Host", new URL(tbSite.getAddressSeedUrl()).getHost());
		requestHeaders.add("User-Agent", userAgent);
		requestHeaders.add("accept-language", "zh-CN,zh;q=0.8,en;q=0.6");
		requestHeaders.add("Referer", "https://i.taobao.com/my_taobao.htm");
		requestHeaders.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		requestHeaders.add("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
		if (tbSite.isUseProxy()) {
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			requestFactory.setProxy(new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(proxy.getProxyIp(), Integer.parseInt(proxy.getProxyPort()))));
			requestFactory.setConnectTimeout(10000);
			requestFactory.setReadTimeout(10000);
			this.restTemplate.setRequestFactory(requestFactory);

		}
		String crawlData = null;
		ResponseEntity<String> response = restTemplate.exchange(tbSite.getAddressSeedUrl(), HttpMethod.GET,
				requestEntity, String.class);
		if (response != null && response.getStatusCode().value() == HttpStatus.OK.value()) {
			String body = response.getBody();
			if (body == null) {
				LOGGER.info("没有获取到数据");
				return null;
			}
			Document document = Jsoup.parse(body);
			Elements elements = document.getElementsByClass("tbl-deliver-address");
			if (elements.size() > 0) {
				crawlData = elements.first().html();
			}

		}
		return crawlData;

	}
}
