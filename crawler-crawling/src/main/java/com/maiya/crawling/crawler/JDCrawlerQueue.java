package com.maiya.crawling.crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 京东抓取任务队列
 * @author xiangdefei
 *
 */
public class JDCrawlerQueue {

	public static final Logger LOGGER = LoggerFactory.getLogger(JDCrawlerQueue.class);

	private JDCrawlerQueue() {

	}

	private static class JDCrawlerQueueHolder {

		private static final JDCrawlerQueue INSTANCE = new JDCrawlerQueue();

	}

	public static JDCrawlerQueue getInstance() {

		return JDCrawlerQueueHolder.INSTANCE;
	}

	private static BlockingQueue<JDCrawlingWorker> queue = new LinkedBlockingQueue<JDCrawlingWorker>(1000);

	public void add(JDCrawlingWorker worker) {
		
			try {
				LOGGER.info("添加任务到京东任务队列开始,当前大小："+queue.size());
				queue.put(worker);
				LOGGER.info("添加任务到京东任务队列结束,当前大小："+queue.size());
			} catch (InterruptedException ignored) {
			}
		
	}

	public JDCrawlingWorker getWorker() {

		JDCrawlingWorker worker = null;
		try {
			worker = queue.take();
		} catch (InterruptedException e) {

		}
		return worker;
	}


	public int size() {

		return queue.size();
	}

}
