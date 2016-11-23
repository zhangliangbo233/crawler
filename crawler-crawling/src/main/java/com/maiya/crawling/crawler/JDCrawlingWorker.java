package com.maiya.crawling.crawler;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.maiya.common.constants.HbaseConstants;
import com.maiya.common.enums.AuthorStatusEnum;
import com.maiya.common.util.HbaseUtil;
import com.maiya.common.util.MaskUtil;
import com.maiya.common.util.MqMessageSender;
import com.maiya.crawling.constants.CrawlingConstants;
import com.maiya.crawling.dto.JDAccount;
import com.maiya.crawling.dto.JDParam;
import com.maiya.crawling.service.AccountService;
import com.maiya.crawling.service.CrawlB2CRetryTaskService;
import com.maiya.crawling.service.CrawlRecordService;
import com.maiya.crawling.service.JDCookieService;
import com.maiya.dal.dao.credit.JobInfoDao;
import com.maiya.dal.dao.credit.MemberIntenetInfoDao;
import com.maiya.dal.dao.myCrawler.JDSiteDao;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.dal.model.JDSite;
import com.maiya.dal.model.JobInfo;
import com.maiya.proxy.service.CrawlProxyService;

public class JDCrawlingWorker implements Runnable {

	public static final Logger LOGGER = LoggerFactory.getLogger(JDCrawlingWorker.class);

	private JDAddressInfoCrawler jdDeliverAddressCrawler;

	private JDCookieService jdCookieService;

	private JDSiteDao jdSiteDao;

	private JDOrderCrawler jdCrawler;

	private MemberIntenetInfoDao memberIntenetInfoDao;

	private AccountService accountService;

	private CrawlProxyService crawlProxyService;

	private HbaseUtil hbaseUtil;

	private String userIndetity;

	private String userChannel;

	private String userCity;

	private Long taskId;

	private JDParam jdParam;

	private JobInfoDao jobInfoDao;

	private MqMessageSender mqMessageSender;

	private CrawlRecordService crawlRecordService;

	private CrawlB2CRetryTaskService crawlB2CRetryTaskService;

	public JDCrawlingWorker(JDAddressInfoCrawler jdDeliverAddressCrawler, JDOrderCrawler jdCrawler,
			JDCookieService jdCookieService, CrawlRecordService crawlRecordService,
			CrawlB2CRetryTaskService crawlB2CRetryTaskService, JDSiteDao jdSiteDao, MqMessageSender mqMessageSender,
			MemberIntenetInfoDao memberIntenetInfoDao, JobInfoDao jobInfoDao, AccountService accountService,
			CrawlProxyService crawlProxyService, HbaseUtil hbaseUtil, String userIndetity, String userChannel,
			String userCity, Long taskId, JDParam jdParam) {

		this.jdDeliverAddressCrawler = jdDeliverAddressCrawler;
		this.jdCrawler = jdCrawler;
		this.jdCookieService = jdCookieService;
		this.crawlRecordService = crawlRecordService;
		this.crawlB2CRetryTaskService = crawlB2CRetryTaskService;
		this.jdSiteDao = jdSiteDao;
		this.mqMessageSender = mqMessageSender;
		this.accountService = accountService;
		this.crawlProxyService = crawlProxyService;
		this.memberIntenetInfoDao = memberIntenetInfoDao;
		this.jobInfoDao = jobInfoDao;
		this.hbaseUtil = hbaseUtil;
		this.userIndetity = userIndetity;
		this.userChannel = userChannel;
		this.taskId = taskId;
		this.userCity = userCity;
		this.jdParam = jdParam;

	}

	@Override
	public void run() {

		JDSite jdSite = this.jdSiteDao.getJDSite();

		JDAccount account = this.accountService.getJDAccount(userIndetity, userChannel);

		if (account == null) {
			LOGGER.info("查询不到当前用户信息,userIndetity:{}", userIndetity);
			return;
		}
		String username = account.getUserName();
		CrawlProxy proxy = null;
		String city = null;

		LOGGER.info("is use proxy:{}", jdSite.isUseProxy());
		if (jdSite.isUseProxy()) {
			JobInfo jobInfo = this.jobInfoDao.getJobInfoByUserId(this.userIndetity);
			if (jobInfo != null) {
				city = jobInfo.getsCity();
			}
			proxy = this.crawlProxyService.getAvailableProxy(city);
			if (proxy == null) {
				LOGGER.warn("没有获取到当前城市的代理IP,city:{}", city);
				proxy = this.crawlProxyService.getAvailableProxy(null);
				if (proxy == null) {
					LOGGER.info("没有获取到任何代理IP");
					if (taskId == null) {

						crawlB2CRetryTaskService.saveRetryTask(this.userIndetity, userChannel, "jd", "获取不到代理");

					} else {

						crawlB2CRetryTaskService.updateRetryTask(taskId, false);
					}
					crawlRecordService.saveCrawlRecord("jd", this.userIndetity, username, 0);

					return;
				}

			} else {
				LOGGER.info("获取的代理成功city:{},ip:{}", city, proxy.toString());
			}

		}

		String cookie = this.jdCookieService.getCookie(jdSite, username, account.getPassword(), userIndetity,
				userChannel, proxy, taskId);

		if (StringUtils.isEmpty(cookie)) {

			LOGGER.error("获取cookie失败,userIndetity:{},username:{}", userIndetity, username);

			if (CrawlingConstants.CHANNEL_MY.equals(userChannel)) {

				memberIntenetInfoDao.updateJDAuthorFlagByUserId(userIndetity, AuthorStatusEnum.FAIL.getStatus());
			}
			crawlRecordService.saveCrawlRecord("jd", this.userIndetity, username, 0);

		} else {

			LOGGER.info("获取cookie成功,userIndetity:{},username:{}", userIndetity, username);

			this.memberIntenetInfoDao.updateJDAuthorFlagByUserId(this.userIndetity,
					AuthorStatusEnum.SUCCESS.getStatus());

			crawlRecordService.saveCrawlRecord("jd", this.userIndetity, username, 1);

			String addressHtml = null;

			try {

				addressHtml = this.jdDeliverAddressCrawler.startCrawl(cookie, jdSite, userIndetity, userChannel,
						username, userCity, taskId, proxy);
			} catch (Exception ex) {
				LOGGER.error("爬取京东收货地址信息失败,userIndetity:{},username:{},exception:{}", userIndetity, username, ex);
				if (taskId == null) {

					crawlB2CRetryTaskService.saveRetryTask(userIndetity, userChannel, "jd", ex.getMessage());

				} else {

					crawlB2CRetryTaskService.updateRetryTask(taskId, false);

				}

				crawlRecordService.upateCrawlAddressFlag("jd", userIndetity, username, 0);

			}

			if (StringUtils.isNotEmpty(addressHtml)) {

				crawlRecordService.upateCrawlAddressFlag("jd", userIndetity, username, 1);

				if (taskId != null) {

					crawlB2CRetryTaskService.updateRetryTask(taskId, true);
				}

				try {
					LOGGER.info("保存京东收货地址信息数据到HBASE开始");
					String rowKey = userIndetity + "_" + userChannel;
					hbaseUtil.putData(HbaseConstants.HBASE_TABLE_ADDRESS, rowKey, HbaseConstants.HBASE_FAMILY_ADDRESS,
							new String[] { HbaseConstants.HBASE_QUALIFIERS_JD_ORIGINAL_ADDRESS }, addressHtml);
					LOGGER.info("保存京东收货地址信息数据到HBASE结束");

					LOGGER.info("发送MQ消息开始");
					String mqMessage = rowKey + "#" + username;
					this.mqMessageSender.send("maiya_myCrawler_jd_address", mqMessage);
					LOGGER.info("发送MQ消息结束");

				} catch (IOException e) {
					LOGGER.error("保存京东收货地址信息数据到HBASE出现异常,exception", e);

				}

				if (StringUtils.equals(this.jdParam.getIsCrawlingOrder(), CrawlingConstants.IS_CRAWLING_JD_ORDER)) {
					try {

						this.jdCrawler.startCraw(true, cookie, jdParam, jdSite, userIndetity, userChannel, hbaseUtil,
								proxy);
						crawlRecordService.updateCrawlOrderFlag("jd", userIndetity, username, 1);
					} catch (Exception ex) {

						LOGGER.error("爬取京东订单数据信息失败,userIndetity:{},username:{},excetpion:{}", userIndetity, username,
								ex);

						if (taskId == null) {

							crawlB2CRetryTaskService.saveRetryTask(userIndetity, userChannel, "jd", ex.getMessage());

						} else {

							crawlB2CRetryTaskService.updateRetryTask(taskId, false);

						}

						crawlRecordService.updateCrawlOrderFlag("jd", userIndetity, username, 0);
					}

				}
			}

		}
	}

}
