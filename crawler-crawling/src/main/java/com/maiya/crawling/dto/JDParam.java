package com.maiya.crawling.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JDParam {
	
	
	@Value("${easybuy.jd.com.host}")
	private String orderJdHost;

	@Value("${is.crawling.jd.order}")
	private String isCrawlingOrder;
	
	@Value("${crawling.jd.order.topN}")
	private int crawlingOrderTopN;
	
	@Value("${crawling.jd.order.thread}")
	private int crawlingOrderThread;
	
	

	public String getOrderJdHost() {
		return orderJdHost;
	}

	public void setOrderJdHost(String orderJdHost) {
		this.orderJdHost = orderJdHost;
	}

	public String getIsCrawlingOrder() {
		return isCrawlingOrder;
	}

	public void setIsCrawlingOrder(String isCrawlingOrder) {
		this.isCrawlingOrder = isCrawlingOrder;
	}

	public int getCrawlingOrderTopN() {
		return crawlingOrderTopN;
	}

	public void setCrawlingOrderTopN(int crawlingOrderTopN) {
		this.crawlingOrderTopN = crawlingOrderTopN;
	}

	public int getCrawlingOrderThread() {
		return crawlingOrderThread;
	}

	public void setCrawlingOrderThread(int crawlingOrderThread) {
		this.crawlingOrderThread = crawlingOrderThread;
	}

}
