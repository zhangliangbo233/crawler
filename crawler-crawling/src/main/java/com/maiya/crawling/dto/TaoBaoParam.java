package com.maiya.crawling.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TaoBaoParam {

	
	@Value("${is.crawling.taobao.order}")
	private String isCrawlingOrder;
	

	@Value("${crawling.taobao.order.topN}")
	private int crawlingOrderTopN;
	
	@Value("${crawling.taobao.order.thread}")
	private int crawlingOrderThread;

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
