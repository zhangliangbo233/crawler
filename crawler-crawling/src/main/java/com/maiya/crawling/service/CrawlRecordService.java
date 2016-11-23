package com.maiya.crawling.service;

import java.util.Date;

import com.maiya.dal.model.CrawlRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maiya.dal.dao.myCrawler.CrawlRecordDao;

@Component
public class CrawlRecordService {

	public static final Logger LOGGER = LoggerFactory.getLogger(CrawlRecordService.class);

	@Autowired
	private CrawlRecordDao crawlRecordDao;

	public void saveCrawlRecord(String site, String userIndetity, String username, int loginFlag) {
		CrawlRecord record = crawlRecordDao.findRecordByUserIdentity(userIndetity, site);
		if (record == null) {
			record = new CrawlRecord();
			record.setSite(site);
			record.setUserIdentity(userIndetity);
			record.setLoginFlag(loginFlag);
			record.setCreateTime(new Date());
			record.setUserName(username);
			crawlRecordDao.insertRecord(record);
		} else {
			record.setLoginFlag(loginFlag);
			record.setUpdateTime(new Date());
			record.setUserName(username);
			crawlRecordDao.updateRecord(record);

		}

	}

	public void upateCrawlAddressFlag(String site, String userIndetity, String username,int flag) {

		LOGGER.info("修改抓取收货地址标记开始,site:{},userIndetity:{}",site,userIndetity);

		try {
			CrawlRecord record = crawlRecordDao.findRecordByUserIdentity(userIndetity, site);
			if (record != null) {
				record.setCrawlAddressFlag(flag);
				record.setUserName(username);
				record.setUpdateTime(new Date());
				crawlRecordDao.updateRecord(record);
			}
		}catch (Exception ex){

			LOGGER.info("修改抓取收货地址标记异常,site:{},userIndetity:{}",site,userIndetity);


		}
		LOGGER.info("修改抓取收货地址标记结束,site:{},userIndetity:{}",site,userIndetity);
	}

	public void updateCrawlOrderFlag(String site, String userIndetity, String userName,int flag) {

		LOGGER.info("修改抓取订单标记开始,site:{},userIndetity:{}",site,userIndetity);

		try {
			CrawlRecord record = crawlRecordDao.findRecordByUserIdentity(userIndetity, site);
			if (record != null) {
				record.setCrawlOrderFlag(flag);
				record.setUserName(userName);
				record.setUpdateTime(new Date());
				crawlRecordDao.updateRecord(record);
			}
		}catch (Exception ex){

			LOGGER.info("修改抓取订单标记异常,site:{},userIndetity:{}",site,userIndetity);

		}
		LOGGER.info("修改抓取订单标记结束,site:{},userIndetity:{}",site,userIndetity);

	}

}
