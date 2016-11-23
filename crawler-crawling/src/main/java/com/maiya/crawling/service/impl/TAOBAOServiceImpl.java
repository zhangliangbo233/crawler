package com.maiya.crawling.service.impl;

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
import com.maiya.crawling.crawler.TaoBaoAddressInfoCrawler;
import com.maiya.crawling.crawler.TaoBaoCrawler;
import com.maiya.crawling.crawler.TaoBaoCrawlerQueue;
import com.maiya.crawling.crawler.TaoBaoCrawlingWorker;
import com.maiya.crawling.crawler.TaoBaoOrderCrawler;
import com.maiya.crawling.crawler.TaobaoAddressCrawler;
import com.maiya.crawling.dto.AddressInfoDoc;
import com.maiya.crawling.dto.TaoBaoOrder;
import com.maiya.crawling.dto.TaoBaoParam;
import com.maiya.crawling.service.AccountService;
import com.maiya.crawling.service.CrawlB2CRetryTaskService;
import com.maiya.crawling.service.CrawlRecordService;
import com.maiya.crawling.service.TAOBAOCookieService;
import com.maiya.crawling.service.TAOBAOService;
import com.maiya.dal.dao.credit.JobInfoDao;
import com.maiya.dal.dao.credit.MemberIntenetInfoDao;
import com.maiya.dal.dao.myCrawler.TAOBAOSiteDao;
import com.maiya.proxy.service.CrawlProxyService;

/**
 * 
 * @author xiangdf
 *
 */
@Service
public class TAOBAOServiceImpl implements TAOBAOService, InitializingBean {

	public static final Logger LOGGER = LoggerFactory.getLogger(TAOBAOServiceImpl.class);

	@Autowired
	private TAOBAOSiteDao taoBaoSiteDao;

	@Autowired
	private MemberIntenetInfoDao memberIntenetInfoDao;

	@Autowired
	private HbaseUtil hbaseUtil;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private TAOBAOCookieService taoBaoCookieService;

	@Autowired
	private TaoBaoOrderCrawler taobaoCrawler;

	@Autowired
	private TaoBaoAddressInfoCrawler taoBaoAddressCrawler;

	@Autowired
	private CrawlProxyService crawlProxyService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private MqMessageSender mqMessageSender;

	@Value("${taobao.mq.topic}")
	private String taobaoMqTopic;

	@Autowired
	private TaoBaoParam taobaoParam;

	@Autowired
	private JobInfoDao jobInfoDao;

	@Autowired
	private CrawlRecordService crawlRecordService;

	@Autowired
	private CrawlB2CRetryTaskService crawlB2CRetryTaskService;

	@Autowired
	private TaobaoAddressCrawler taobaoAddressCrawler;

	@Value("${taobao.crawl.thread.size}")
	private int theadSize;

	@Override
	public void afterPropertiesSet() throws Exception {

		taskExecutor.execute(new TaoBaoCrawler(taskExecutor, theadSize));

	}

	@Override
	public BaseResponse crawlingTaoBao(String userIdentity, String userChannel, String city, Long taskId) {

		BaseResponse response = new BaseResponse();

		if (StringUtils.isAnyEmpty(userIdentity, userChannel)) {

			LOGGER.error("illegal argument,userIdentity or userChannel  can not be blank ");

			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.INVALID_PARAMETERS.getMessage());

			return response;
		}

		response.setRetcode(ReturnCodeEnum.SUCCESS.getCode());

		response.setRetinfo(ReturnCodeEnum.SUCCESS.getMessage());

		TaoBaoCrawlerQueue.getInstance()
				.add(new TaoBaoCrawlingWorker(taoBaoAddressCrawler, taobaoAddressCrawler, taobaoCrawler,
						taoBaoCookieService, crawlRecordService, crawlB2CRetryTaskService, taoBaoSiteDao,
						memberIntenetInfoDao, jobInfoDao, accountService, crawlProxyService, hbaseUtil, mqMessageSender,
						taobaoMqTopic, userIdentity, userChannel, city, taskId, taobaoParam));

		//
		// taskExecutor.execute(new
		// TaoBaoCrawlingWorker(taoBaoAddressCrawler,taobaoAddressCrawler,
		// taobaoCrawler,
		// taoBaoCookieService,crawlRecordService,crawlB2CRetryTaskService,
		// taoBaoSiteDao, memberIntenetInfoDao, jobInfoDao,accountService,
		// crawlProxyService, hbaseUtil, mqMessageSender,
		// taobaoMqTopic, userIdentity, userChannel, city, taskId,taobaoParam));

		return response;

	}

	@Override
	public BaseResponse queryTaoBaoOrders(String userIdentity, String userChannel) throws Exception {

		BaseResponse response = new BaseResponse();

		if (StringUtils.isAnyEmpty(userIdentity, userChannel)) {

			LOGGER.error("illegal argument,userIdentity or userChannel  can not be blank ");

			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.INVALID_PARAMETERS.getMessage());

			return response;
		}

		List<String> resultList = hbaseUtil.getDataByPrefix(HbaseConstants.HBASE_TABLE_TAOBAO,
				userIdentity + "_" + userChannel, HbaseConstants.HBASE_QUALIFIERS_ANALYSISMESSAGE);

		List<List<TaoBaoOrder>> docList = new ArrayList<List<TaoBaoOrder>>();

		for (String jsonData : resultList) {
			List<TaoBaoOrder> orders = JSONArray.parseArray(jsonData, TaoBaoOrder.class);
			docList.add(orders);
		}

		response.setRetcode(ReturnCodeEnum.SUCCESS.getCode());

		response.setRetinfo(ReturnCodeEnum.SUCCESS.getMessage());

		response.setDoc(docList);

		return response;

	}

	@Override
	public BaseResponse queryTaoBaoAddressInfo(String userIdentity, String userChannel) {

		BaseResponse response = new BaseResponse();

		if (StringUtils.isAnyEmpty(userIdentity, userChannel)) {

			LOGGER.error("illegal argument,userIdentity or userChannel  can not be blank ");

			response.setRetcode(ReturnCodeEnum.FAIL.getCode());

			response.setRetinfo(ReturnCodeEnum.INVALID_PARAMETERS.getMessage());

			return response;
		}

		String jsonData = null;
		try {
			jsonData = hbaseUtil.getData(HbaseConstants.HBASE_TABLE_ADDRESS, userIdentity + "_" + userChannel,
					HbaseConstants.HBASE_QUALIFIERS_TAOBAO_ADDRESS);

			AddressInfoDoc result = JSONArray.parseObject(jsonData, AddressInfoDoc.class);

			response.setDoc(result);

			response.setRetcode(ReturnCodeEnum.SUCCESS.getCode());

			response.setRetinfo(ReturnCodeEnum.SUCCESS.getMessage());

		} catch (Exception e) {
			LOGGER.error("查询HBASE出现异常,exception:{}", e);
		}

		return response;
	}

}
