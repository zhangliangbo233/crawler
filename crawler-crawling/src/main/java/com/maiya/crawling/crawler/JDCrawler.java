package com.maiya.crawling.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class JDCrawler implements Runnable {

	public static final Logger LOGGER = LoggerFactory.getLogger(JDCrawler.class);

	private ThreadPoolTaskExecutor taskExecutor;

	private int threadSize;

	public JDCrawler(ThreadPoolTaskExecutor taskExecutor, int threadSize) {

		this.taskExecutor = taskExecutor;
		this.threadSize = threadSize;
	}

	@Override
	public void run() {
		while (true) {
			JDCrawlerQueue queue = JDCrawlerQueue.getInstance();
			LOGGER.info("京东爬取任务队列大小:" + queue.size());
			for (int i = 0; i < threadSize; i++) {
				JDCrawlingWorker worker = queue.getWorker();
				LOGGER.info("从京东爬取任务队列中获取到任务");
				taskExecutor.execute(worker);

			}

		}
	}

}
