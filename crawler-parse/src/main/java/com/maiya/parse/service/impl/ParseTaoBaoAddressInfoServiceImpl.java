package com.maiya.parse.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.maiya.common.annotations.MqConfig;
import com.maiya.common.constants.HbaseConstants;
import com.maiya.common.util.HbaseUtil;
import com.maiya.parse.service.ParseTaoBaoAddressInfoService;

@Service
@MqConfig(destination = "parseTaoBaoAddressCrawlDataQueue", destinationPhysicalName = "maiya_myCrawler_taobao_address")
public class ParseTaoBaoAddressInfoServiceImpl implements ParseTaoBaoAddressInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParseTaoBaoAddressInfoServiceImpl.class);

	@Autowired
	private HbaseUtil hbaseUtil;

	@Override
	public void onMessage(Message message) {

		TextMessage content = (TextMessage) message;

		try {
			String text = content.getText();

			LOGGER.info("监听到消息:" + text);

			parseData(text);

		} catch (Exception e) {

			LOGGER.error("get message exception", e);
		}

	}

	public void parseData(String message) {

		String messages[] = message.split("#");
		String rowKey = messages[0];
		String username = messages[1];

		LOGGER.info("解析京东收货地址信息开始,rowKey:{},username:{}", rowKey, username);
		String addressStr = null;
		try {
			addressStr = hbaseUtil.getData(HbaseConstants.HBASE_TABLE_ADDRESS, rowKey,
					HbaseConstants.HBASE_QUALIFIERS_TAOBAO_ORIGINAL_ADDRESS);
		} catch (IOException ex) {
			LOGGER.error("从HBASE查询数据出现异常", ex);
			return;
		}

		if (StringUtils.isBlank(addressStr)) {
			LOGGER.info("收货地址信息为空");
			return;
		}

		Document document = Jsoup.parse(addressStr);

		Elements elements = document.getElementsByClass("thead-tbl-address");
		if (elements.size() == 0) {
			LOGGER.info("没有收货地址");
			return;
		}

		if (elements.size() > 0) {

			Map<String, Object> resultMap = new HashMap<String, Object>();
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			Map<String, Object> dataMap = null;
			for (Element element : elements) {

				dataMap = new HashMap<String, Object>();
				Elements tdElements = element.getElementsByTag("td");

				StringBuffer sb = new StringBuffer();
				for (Element tdElement : tdElements) {
					sb.append(tdElement.text());
					sb.append(",");
				}
				String[] datas = sb.toString().split(",");
				dataMap.put("name", datas[0]);
				dataMap.put("address", datas[1] + datas[2]);
				dataMap.put("mobile", datas[4]);
				if (StringUtils.equals(datas[6], "默认地址")) {
					dataMap.put("isDefault", true);
				} else {
					dataMap.put("isDefault", false);
				}
				dataList.add(dataMap);
			}

			resultMap.put("username", username);

			resultMap.put("addressInfo", dataList);

			String jsonData = JSON.toJSONString(resultMap);

			LOGGER.info("解析后淘宝收货地址信息,jsonData:{}", jsonData);
			LOGGER.info("解析淘宝收货地址信息结束,rowKey:{},username:{}", rowKey, username);

			try {
				LOGGER.info("保存解析后淘宝收货地址信息开始");
				hbaseUtil.putData(HbaseConstants.HBASE_TABLE_ADDRESS, rowKey, HbaseConstants.HBASE_FAMILY_ADDRESS,
						new String[] { HbaseConstants.HBASE_QUALIFIERS_TAOBAO_ADDRESS }, jsonData);
				LOGGER.info("保存解析后淘宝收货地址信息结束");

			} catch (IOException e) {
				LOGGER.error("保存解析后淘宝收货地址信息异常", e);
			}

		}
	}

}
