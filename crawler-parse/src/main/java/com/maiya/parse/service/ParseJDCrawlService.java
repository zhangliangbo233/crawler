package com.maiya.parse.service;

import javax.jms.MessageListener;

public interface ParseJDCrawlService extends MessageListener{

	
	public void parseData(String rowkey);
	
}
