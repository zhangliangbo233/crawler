package com.maiya.parse.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maiya.common.annotations.MqConfig;
import com.maiya.common.constants.HbaseConstants;
import com.maiya.common.util.HbaseUtil;
import com.maiya.common.util.JsonUtil;
import com.maiya.parse.service.ParseTaoBaoCrawlService;

@Service
@MqConfig(destination = "parseTaoBaoCrawlDataQueue", destinationPhysicalName = "maiya_taobaoCrawlData_myCrawler")
public class ParseTaoBaoCrawServiceImpl implements ParseTaoBaoCrawlService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParseTaoBaoCrawServiceImpl.class);

	@Autowired
	private HbaseUtil hbaseUtil;

	@Override
	public void onMessage(Message message) {

		TextMessage content = (TextMessage) message;

		try {
			String rowkey = content.getText();

			parseData(rowkey);

		} catch (Exception e) {

			LOGGER.error("get message exception", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void parseData(String rowkey) {

		try {
			String jsonData = hbaseUtil.getData(HbaseConstants.HBASE_TABLE_TAOBAO, rowkey,
					HbaseConstants.HBASE_QUALIFIERS_INFOMESSAGE);

			Map<String, Object> body = (Map<String, Object>) JsonUtil.jsonToMap(jsonData);
			
			List<Map<String, Object>> mainOrders = (List<Map<String, Object>>) body.get("mainOrders");
			
			List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
			
			Map<String,Object> resultMap=null;
			
			for (Map<String, Object> map : mainOrders) {

				List<Map<String, Object>> subOrders = (List<Map<String, Object>>) map.get("subOrders");

				for (Map<String, Object> map2 : subOrders) {

					resultMap=new HashMap<String,Object>();

					resultMap.put("title", ((Map<String, Object>) map2.get("itemInfo")).get("title"));
					
					resultMap.put("priceInfo", ((Map<String, Object>) map2.get("priceInfo")).get("realTotal"));
					
					resultMap.put("createTime", ((Map<String, Object>) map.get("orderInfo")).get("createTime"));

					resultList.add(resultMap);
					
				}

			}
			
			String dataJson=JsonUtil.objectToJson(resultList);
			
			hbaseUtil.putData(HbaseConstants.HBASE_TABLE_TAOBAO, rowkey, HbaseConstants.HBASE_FAMILY,
					new String[] { HbaseConstants.HBASE_QUALIFIERS_ANALYSISMESSAGE }, dataJson);

		} catch (IOException e) {
			LOGGER.error("parse data exception ", e);
		}

	}

}
