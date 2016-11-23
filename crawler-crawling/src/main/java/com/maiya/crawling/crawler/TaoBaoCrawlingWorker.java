package com.maiya.crawling.crawler;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiya.common.constants.HbaseConstants;
import com.maiya.common.enums.AuthorStatusEnum;
import com.maiya.common.util.HbaseUtil;
import com.maiya.common.util.MaskUtil;
import com.maiya.common.util.MqMessageSender;
import com.maiya.crawling.constants.CrawlingConstants;
import com.maiya.crawling.dto.TaoBaoAccount;
import com.maiya.crawling.dto.TaoBaoParam;
import com.maiya.crawling.service.AccountService;
import com.maiya.crawling.service.CrawlB2CRetryTaskService;
import com.maiya.crawling.service.CrawlRecordService;
import com.maiya.crawling.service.TAOBAOCookieService;
import com.maiya.dal.dao.credit.JobInfoDao;
import com.maiya.dal.dao.credit.MemberIntenetInfoDao;
import com.maiya.dal.dao.myCrawler.TAOBAOSiteDao;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.dal.model.JobInfo;
import com.maiya.dal.model.TAOBAOSite;
import com.maiya.proxy.service.CrawlProxyService;

public class TaoBaoCrawlingWorker implements Runnable {

	public static final Logger LOGGER = LoggerFactory.getLogger(TaoBaoCrawlingWorker.class);

	private TaoBaoAddressInfoCrawler taoBaoAddressInfoCrawler;

	private TAOBAOSiteDao taoBaoSiteDao;

	private MemberIntenetInfoDao memberIntenetInfoDao;

	private AccountService accountService;

	private TAOBAOCookieService taoBaoCookieService;

	private CrawlProxyService crawlProxyService;

	private TaoBaoOrderCrawler taobaoCrawler;

	private HbaseUtil hbaseUtil;

	private String userIndetity;

	private String userChannel;

	private MqMessageSender mqMessageSender;

	private String taobaoMqTopic;

	private String city;

	private Long taskId;

	private TaoBaoParam taobaoParam;

	private JobInfoDao jobInfoDao;

	private CrawlRecordService crawlRecordService;

	private CrawlB2CRetryTaskService crawlB2CRetryTaskService;

	private TaobaoAddressCrawler taobaoAddressCrawler;

	public TaoBaoCrawlingWorker(TaoBaoAddressInfoCrawler taoBaoAddressInfoCrawler,
			TaobaoAddressCrawler taobaoAddressCrawler, TaoBaoOrderCrawler taobaoCrawler,
			TAOBAOCookieService taoBaoCookieService, CrawlRecordService crawlRecordService,
			CrawlB2CRetryTaskService crawlB2CRetryTaskService, TAOBAOSiteDao taoBaoSiteDao,
			MemberIntenetInfoDao memberIntenetInfoDao, JobInfoDao jobInfoDao, AccountService accountService,
			CrawlProxyService crawlProxyService, HbaseUtil hbaseUtil, MqMessageSender mqMessageSender,
			String taobaoMqTopic, String userIndetity, String userChannel, String city, Long taskId,
			TaoBaoParam taobaoParam) {

		this.taoBaoAddressInfoCrawler = taoBaoAddressInfoCrawler;
		this.taobaoAddressCrawler = taobaoAddressCrawler;
		this.taoBaoSiteDao = taoBaoSiteDao;
		this.accountService = accountService;
		this.crawlProxyService = crawlProxyService;
		this.memberIntenetInfoDao = memberIntenetInfoDao;
		this.jobInfoDao = jobInfoDao;
		this.taoBaoCookieService = taoBaoCookieService;
		this.crawlRecordService = crawlRecordService;
		this.crawlB2CRetryTaskService = crawlB2CRetryTaskService;
		this.taobaoCrawler = taobaoCrawler;
		this.hbaseUtil = hbaseUtil;
		this.userIndetity = userIndetity;
		this.mqMessageSender = mqMessageSender;
		this.taobaoMqTopic = taobaoMqTopic;
		this.userChannel = userChannel;
		this.city = city;
		this.taskId = taskId;
		this.taobaoParam = taobaoParam;

	}

	@Override
	public void run() {

		TAOBAOSite tbSite = this.taoBaoSiteDao.getTAOBAOSite();

		TaoBaoAccount tbAccount = this.accountService.getTaoBaoAccount(userIndetity, userChannel);

		if (tbAccount == null) {
			LOGGER.info("查询不到当前用户信息,userIndetity:{}", userIndetity);
			return;
		}
		String userName = tbAccount.getUserName();
		CrawlProxy proxy = null;
		String city = null;
		LOGGER.info("is use proxy:{}", tbSite.isUseProxy());
		if (tbSite.isUseProxy()) {
			proxy = this.crawlProxyService.getAvailableProxy(city);
			if (proxy == null) {
				LOGGER.info("没有获取到任何代理IP");
				if (taskId == null) {

					crawlB2CRetryTaskService.saveRetryTask(this.userIndetity, userChannel, "taobao", "获取不到代理");

				} else {

					crawlB2CRetryTaskService.updateRetryTask(taskId, false);
				}
				crawlRecordService.saveCrawlRecord("taobao", this.userIndetity, userName, 0);
				return;
			}
			LOGGER.info("获取的代理成功,ip:{}", proxy.toString());

		}

		String cookie = this.taoBaoCookieService.getCookie(tbSite, userName, tbAccount.getPassword(), userIndetity,
				userChannel, proxy, taskId);

		if (StringUtils.isEmpty(cookie)) {

			LOGGER.info("获取cookie失败,userIndetity:{},userName:{}", userIndetity, userName);

			if (CrawlingConstants.CHANNEL_MY.equals(userChannel)) {

				memberIntenetInfoDao.updateTaoBaoAuthorFlagByUserId(userIndetity, AuthorStatusEnum.FAIL.getStatus());
			}

			crawlRecordService.saveCrawlRecord("taobao", this.userIndetity, userName, 0);
		} else {

			LOGGER.info("获取cookie成功,userIndetity:{},userName:{}", userIndetity, userName);

			this.memberIntenetInfoDao.updateTaoBaoAuthorFlagByUserId(this.userIndetity,
					AuthorStatusEnum.SUCCESS.getStatus());

			crawlRecordService.saveCrawlRecord("taobao", this.userIndetity, userName, 1);

			String crawlData = null;
			try {
				crawlData = taobaoAddressCrawler.startCrawl(cookie, tbSite, userIndetity, userChannel, proxy);

				// crawlData=taoBaoAddressInfoCrawler.startCrawl(cookie, tbSite,
				// proxy);
			} catch (Exception ex) {

				LOGGER.error("爬取淘宝收货地址信息失败,userIndetity:{},username:{},exception:{}", userIndetity, userName, ex);

				if (taskId == null) {

					crawlB2CRetryTaskService.saveRetryTask(this.userIndetity, userChannel, "taobao", ex.getMessage());

				} else {

					crawlB2CRetryTaskService.updateRetryTask(taskId, false);
				}

				crawlRecordService.upateCrawlAddressFlag("taobao", userIndetity, userName, 0);
			}

			if (StringUtils.isNotBlank(crawlData)) {

				crawlRecordService.upateCrawlAddressFlag("taobao", userIndetity, userName, 1);

				if (taskId != null) {

					crawlB2CRetryTaskService.updateRetryTask(taskId, true);
				}
				String rowKey = userIndetity + "_" + userChannel;
				try {
					LOGGER.info("保存淘宝收货地址信息到hbase开始");
					hbaseUtil.putData(HbaseConstants.HBASE_TABLE_ADDRESS, rowKey, HbaseConstants.HBASE_FAMILY_ADDRESS,
							new String[] { HbaseConstants.HBASE_QUALIFIERS_TAOBAO_ORIGINAL_ADDRESS }, crawlData);
					LOGGER.info("保存淘宝收货地址信息到hbase结束");
				} catch (IOException e) {
					LOGGER.error("保存淘宝收货地址信息到hbase异常", e);

				}

				LOGGER.info("发送MQ消息开始");
				String mqMessage = rowKey + "#" + userName;
				this.mqMessageSender.send("maiya_myCrawler_taobao_address", mqMessage);
				LOGGER.info("发送MQ消息结束");

			} else {
				if (taskId == null) {

					crawlB2CRetryTaskService.saveRetryTask(this.userIndetity, userChannel, "taobao", "");

				} else {

					crawlB2CRetryTaskService.updateRetryTask(taskId, false);
				}

				crawlRecordService.upateCrawlAddressFlag("taobao", userIndetity, userName, 0);

			}

			if (StringUtils.equals(taobaoParam.getIsCrawlingOrder(), CrawlingConstants.IS_CRAWLING_TAOBAO_ORDER)) {

				try {
					this.taobaoCrawler.startCrawl(true, cookie, tbSite, hbaseUtil, mqMessageSender, taobaoMqTopic,
							userIndetity, userChannel, proxy, taobaoParam);
				} catch (Exception ex) {

					LOGGER.error("爬取淘宝订单数据信息失败,userIndetity:{},username:{},excetpion:{}", userIndetity, userName, ex);
					if (taskId == null) {

						crawlB2CRetryTaskService.saveRetryTask(userIndetity, userChannel, "jd", ex.getMessage());

					} else {

						crawlB2CRetryTaskService.updateRetryTask(taskId, false);

					}

					crawlRecordService.updateCrawlOrderFlag("jd", userIndetity, userName, 0);
				}
			}

		}
	}

}
