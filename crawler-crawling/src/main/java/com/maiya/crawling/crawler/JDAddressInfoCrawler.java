package com.maiya.crawling.crawler;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

import com.maiya.dal.model.CrawlProxy;
import com.maiya.dal.model.JDSite;

/**
 * 京东收获地址爬取
 * 
 * @author xiangdf
 *
 */
@Component
public class JDAddressInfoCrawler {

	public static final Logger LOGGER = LoggerFactory.getLogger(JDAddressInfoCrawler.class);

	@Value("${firefox.user.agent}")
	private String userAgent;

	@Autowired
	private RestTemplate restTemplate;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String startCrawl(String cookie, JDSite jdSite, String userIdentity, String userChannel, String userName,
			String userCity, Long taskId, CrawlProxy proxy) throws MalformedURLException {
		LOGGER.info("开始爬取京东收货地址信息,userIdentity:{},userName:{}", userIdentity, userName);
		if (StringUtils.isBlank(cookie)) {
			LOGGER.warn("cookie不能为空");
			return null;
		}
		// 设置header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Cookie", cookie);
		requestHeaders.add("Host", new URL(jdSite.getAddressSeedUrl()).getHost());
		requestHeaders.add("User-Agent", userAgent);
		HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
		if (jdSite.isUseProxy()) {
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			requestFactory.setProxy(new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(proxy.getProxyIp(), Integer.parseInt(proxy.getProxyPort()))));
			requestFactory.setConnectTimeout(5000);
			requestFactory.setReadTimeout(5000);
			this.restTemplate.setRequestFactory(requestFactory);

		}

		ResponseEntity<String> response = restTemplate.exchange(jdSite.getAddressSeedUrl(), HttpMethod.GET,
				requestEntity, String.class);
		if (response != null && response.getStatusCode().value() == HttpStatus.OK.value()) {

			String body = response.getBody();
			if (StringUtils.isNotEmpty(body)) {
				Document document = Jsoup.parse(body);

				Element addressListElement = document.getElementById("addressList");

				if (addressListElement != null) {

					return addressListElement.html();
				}
			}

		}

		return null;
	}
}
