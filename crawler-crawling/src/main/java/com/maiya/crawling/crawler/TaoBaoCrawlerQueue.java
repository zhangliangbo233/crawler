package com.maiya.crawling.crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 淘宝抓取任务队列
 * @author xiangdefei
 *
 */
public class TaoBaoCrawlerQueue {

	public static final Logger LOGGER = LoggerFactory.getLogger(TaoBaoCrawlerQueue.class);

	private TaoBaoCrawlerQueue() {

	}

	private static class TaoBaoCrawlerQueueHolder {

		private static final TaoBaoCrawlerQueue INSTANCE = new TaoBaoCrawlerQueue();

	}

	public static TaoBaoCrawlerQueue getInstance() {

		return TaoBaoCrawlerQueueHolder.INSTANCE;
	}

	private static BlockingQueue<TaoBaoCrawlingWorker> queue = new LinkedBlockingQueue<TaoBaoCrawlingWorker>(1000);

	public void add(TaoBaoCrawlingWorker worker) {

		try {
			LOGGER.info("添加任务到淘宝任务队列开始,当前大小：" + queue.size());
			queue.put(worker);
			LOGGER.info("添加任务到淘宝任务队列结束,当前大小：" + queue.size());
		} catch (InterruptedException ignored) {
		}

	}

	public TaoBaoCrawlingWorker getWorker() {

		TaoBaoCrawlingWorker worker = null;
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
