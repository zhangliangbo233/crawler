package com.maiya.crawling.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.maiya.common.constants.HbaseConstants;
import com.maiya.common.dto.BaseResponse;
import com.maiya.common.enums.ReturnCodeEnum;
import com.maiya.common.util.HbaseUtil;
import com.maiya.common.util.MqMessageSender;
import com.maiya.crawling.crawler.JDAddressInfoCrawler;
import com.maiya.crawling.crawler.JDCrawler;
import com.maiya.crawling.crawler.JDCrawlerQueue;
import com.maiya.crawling.crawler.JDCrawlingWorker;
import com.maiya.crawling.crawler.JDOrderCrawler;
import com.maiya.crawling.dto.AddressInfoDoc;
import com.maiya.crawling.dto.JDOrder;
import com.maiya.crawling.dto.JDParam;
import com.maiya.crawling.service.AccountService;
import com.maiya.crawling.service.CrawlB2CRetryTaskService;
import com.maiya.crawling.service.CrawlRecordService;
import com.maiya.crawling.service.JDCookieService;
import com.maiya.crawling.service.JDService;
import com.maiya.dal.dao.credit.JobInfoDao;
import com.maiya.dal.dao.credit.MemberIntenetInfoDao;
import com.maiya.dal.dao.myCrawler.JDSiteDao;
import com.maiya.proxy.service.CrawlProxyService;

/**
 * 
 * @author xiangdf
 *
 */
@Service
public class JDServiceImpl implements JDService, InitializingBean {

	public static final Logger LOGGER = LoggerFactory.getLogger(JDServiceImpl.class);

	@Autowired
	private JDSiteDao jdSiteDao;

	@Autowired
	private MemberIntenetInfoDao memberIntenetInfoDao;

	@Autowired
	private HbaseUtil hbaseUtil;

	@Autowired
	private JDOrderCrawler jdCrawler;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private CrawlProxyService crawlProxyService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private JDAddressInfoCrawler jdDeliverAddressCrawler;

	@Autowired
	private JDCookieService jdCookieService;

	@Autowired
	private JDParam jdParam;

	@Autowired
	private JobInfoDao jobInfoDao;

	@Autowired
	private MqMessageSender mqMessageSender;

	@Autowired
	private CrawlRecordService crawlRecordService;

	@Autowired
	private CrawlB2CRetryTaskService crawlB2CRetryTaskService;
	
	@Value("${jd.crawl.thread.size}")
	private int theadSize; 

	@Override
	public void afterPropertiesSet() throws Exception {
		
		taskExecutor.execute(new JDCrawler(taskExecutor,theadSize));
	}
	
	


	@Override
	public BaseResponse crawlingJD(String userIdentity, String userChannel, String city, Long taskId) {

		BaseResponse response = new BaseResponse();

		if (StringUtils.isAnyEmpty(userIdentity, userChannel)) {

			LOGGER.error("illegal argument,userIdentity or channel can not be blank ");

			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.INVALID_PARAMETERS.getMessage());

			return response;
		}

		response.setRetcode(ReturnCodeEnum.SUCCESS.getCode());

		response.setRetinfo(ReturnCodeEnum.SUCCESS.getMessage());

		JDCrawlerQueue.getInstance().add(new JDCrawlingWorker(jdDeliverAddressCrawler, jdCrawler, jdCookieService, crawlRecordService,
				crawlB2CRetryTaskService, jdSiteDao, mqMessageSender, memberIntenetInfoDao, jobInfoDao, accountService,
				crawlProxyService, hbaseUtil, userIdentity, userChannel, city, taskId, jdParam));

		// taskExecutor.execute(new JDCrawlingWorker(jdDeliverAddressCrawler,
		// jdCrawler,
		// jdCookieService,crawlRecordService,crawlB2CRetryTaskService,jdSiteDao,mqMessageSender,
		// memberIntenetInfoDao, jobInfoDao, accountService, crawlProxyService,
		// hbaseUtil, userIdentity, userChannel, city,
		// taskId,jdParam));

		return response;
	}

	@Override
	public BaseResponse getJDOrders(String userIdentity, String userChannel) throws IOException {
		BaseResponse response = new BaseResponse();

		if (StringUtils.isAnyEmpty(userIdentity, userChannel)) {

			LOGGER.error("illegal argument,userIdentity or channel can not be blank ");

			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.INVALID_PARAMETERS.getMessage());

			return response;
		}

		List<String> resultList = hbaseUtil.getDataByPrefix(HbaseConstants.HBASE_TABLE_JD,
				userIdentity + "_" + userChannel + "_", HbaseConstants.HBASE_QUALIFIERS_ANALYSISMESSAGE);

		LOGGER.info("jsonData:{}", resultList.size());

		List<List<JDOrder>> docList = new ArrayList<List<JDOrder>>();

		for (String jsonData : resultList) {
			List<JDOrder> orders = JSONArray.parseArray(jsonData, JDOrder.class);
			docList.add(orders);
		}

		response.setRetcode(ReturnCodeEnum.SUCCESS.getCode());

		response.setRetinfo(ReturnCodeEnum.SUCCESS.getMessage());

		response.setDoc(docList);

		return response;

	}

	@Override
	public BaseResponse getJDAddressInfo(String userIdentity, String userChannel) {

		BaseResponse response = new BaseResponse();

		if (StringUtils.isAnyEmpty(userIdentity, userChannel)) {

			LOGGER.error("illegal argument,userIdentity or channel can not be blank ");

			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.INVALID_PARAMETERS.getMessage());

			return response;
		}
		String jsonData = null;
		try {
			jsonData = hbaseUtil.getData(HbaseConstants.HBASE_TABLE_ADDRESS, userIdentity + "_" + userChannel,
					HbaseConstants.HBASE_QUALIFIERS_JD_ADDRESS);
		} catch (IOException e) {

			LOGGER.error("查询hbase出现异常,exception:{}", e);
		}

		AddressInfoDoc result = JSONArray.parseObject(jsonData, AddressInfoDoc.class);

		response.setDoc(result);

		response.setRetcode(ReturnCodeEnum.SUCCESS.getCode());

		response.setRetinfo(ReturnCodeEnum.SUCCESS.getMessage());

		return response;
	}

}
