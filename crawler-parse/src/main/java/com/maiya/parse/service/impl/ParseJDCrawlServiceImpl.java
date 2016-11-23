package com.maiya.parse.service.impl;

import java.io.IOException;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maiya.common.annotations.MqConfig;
import com.maiya.common.constants.HbaseConstants;
import com.maiya.common.util.HbaseUtil;
import com.maiya.parse.service.ParseJDCrawlService;

@Service
@MqConfig(destination = "parseJDCrawlDataQueue", destinationPhysicalName = "maiya_jdCrawlData_myCrawler")
public class ParseJDCrawlServiceImpl implements ParseJDCrawlService {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(ParseRestrictCrawlServiceImpl.class);
	
	@Autowired
	private HbaseUtil hbaseUtil;

	@Override
	public void onMessage(Message message) {

		TextMessage content = (TextMessage) message;
		
		try {
			String rowkey = content.getText();

			parseData(rowkey);
			
        } catch (Exception e) {
			
			LOGGER.error("get message exception",e);
		} 

	}
	
	
	public void parseData(String rowkey){
		
			String data;
			
			try {
				data = hbaseUtil.getData(HbaseConstants.HBASE_TABLE_JD, rowkey,
						HbaseConstants.HBASE_QUALIFIERS_INFOMESSAGE);
				
				hbaseUtil.putData(HbaseConstants.HBASE_TABLE_JD, rowkey, HbaseConstants.HBASE_FAMILY,
						new String[] { HbaseConstants.HBASE_QUALIFIERS_ANALYSISMESSAGE }, data);

			} catch (IOException e) {
				LOGGER.error("parse data exception ",e);
			}

			
		
	}
	
	

}
