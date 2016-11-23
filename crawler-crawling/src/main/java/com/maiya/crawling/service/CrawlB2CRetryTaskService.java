package com.maiya.crawling.service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.maiya.common.enums.CrawlTaskStatusEnum;
import com.maiya.dal.dao.myCrawler.CrawlB2CRetryTaskDao;
import com.maiya.dal.model.CrawlB2CRetryTask;

@Component
public class CrawlB2CRetryTaskService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(CrawlB2CRetryTaskService.class);
	
	@Autowired
	private CrawlB2CRetryTaskDao crawlB2CRetryTaskDao;
	
	@Value("${b2c.crawl.fail.task.retry.num}")
	private int maxRetryNum;
	
	/**
	 * 保存失败的任务
	 * 
	 * @param userIdentity
	 * @param userChannel
	 */
	public void saveRetryTask(String userIdentity, String userChannel,String site,String reason) {
		LOGGER.info("保存失败任务开始");
		CrawlB2CRetryTask crawlB2CRetryTask = new CrawlB2CRetryTask();
		crawlB2CRetryTask.setSite(site);
		crawlB2CRetryTask.setUserChannel(userChannel);
		crawlB2CRetryTask.setUserIdentity(userIdentity);
		crawlB2CRetryTask.setStatus(CrawlTaskStatusEnum.FAIL.getCode());
		crawlB2CRetryTask.setCreateTime(new Date());
		crawlB2CRetryTask.setReason(reason);
		crawlB2CRetryTaskDao.insert(crawlB2CRetryTask);
		LOGGER.info("保存失败任务结束");
	}

	/**
	 * 更新重试任务
	 * @param taskId
	 * @param status
	 */
	public void updateRetryTask(Long taskId,boolean status) {
		LOGGER.info("更新失败任务开始");
		CrawlB2CRetryTask crawlB2CRetryTask = crawlB2CRetryTaskDao.findById(taskId);
		LOGGER.info("taskId:{}",crawlB2CRetryTask.getId());
		if (crawlB2CRetryTask != null) {

			if (crawlB2CRetryTask.getRetryNum() >= maxRetryNum) {
				LOGGER.info("已经超过最大重试次数,修改状态为 passed ,taskId is {}", crawlB2CRetryTask.getId());
				crawlB2CRetryTask.setStatus(CrawlTaskStatusEnum.PASSED.getCode());
			} else {
				crawlB2CRetryTask.setRetryNum(new AtomicInteger(crawlB2CRetryTask.getRetryNum()).incrementAndGet());
			}

			crawlB2CRetryTask.setUpdateTime(new Date());
			if (status) {
				crawlB2CRetryTask.setStatus(CrawlTaskStatusEnum.SUCCESS.getCode());
			}
			
			crawlB2CRetryTaskDao.update(crawlB2CRetryTask);

		}
		LOGGER.info("更新失败任务结束");

	}

}
