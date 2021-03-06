package com.maiya.crawling.webcollector.crawler;

import com.maiya.crawling.webcollector.crawldb.DBManager;
import com.maiya.crawling.webcollector.crawldb.Generator;
import com.maiya.crawling.webcollector.fetcher.Fetcher;
import com.maiya.crawling.webcollector.model.CrawlDatum;
import com.maiya.crawling.webcollector.model.Links;
import com.maiya.crawling.webcollector.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiya.crawling.webcollector.fetcher.Executor;
import com.maiya.crawling.webcollector.model.CrawlDatums;


public class Crawler {

    public static final Logger LOG = LoggerFactory.getLogger(Crawler.class);

    public Crawler(){

    }

    /**
     * 根据任务管理器和执行器构造爬虫
     * @param dbManager 任务管理器
     * @param executor 执行器
     */
    public Crawler(DBManager dbManager, Executor executor){
        this.dbManager=dbManager;
        this.executor=executor;
    }

    protected int status;
    public final static int RUNNING = 1;
    public final static int STOPED = 2;
    protected boolean resumable = false;
    protected int threads = 50;

    protected int topN = -1;
    protected long executeInterval = 0;

    protected CrawlDatums seeds = new CrawlDatums();
    protected CrawlDatums forcedSeeds = new CrawlDatums();
    protected Fetcher fetcher;
    protected int maxExecuteCount = -1;

    protected Executor executor = null;
    protected DBManager dbManager;


    protected void inject() throws Exception {
        dbManager.inject(seeds);
    }

    public void injectForcedSeeds() throws Exception {
        dbManager.inject(forcedSeeds, true);
    }

    /**
     * 开始爬取，迭代次数为depth
     * @param depth 迭代次数
     * @throws Exception 异常
     */
    public void start(int depth) throws Exception {

        boolean needInject = true;

        if (resumable && dbManager.isDBExists()) {
            needInject = false;
        }

        if (!resumable) {
            if (dbManager.isDBExists()) {
                dbManager.clear();
            }

            if (seeds.isEmpty() && forcedSeeds.isEmpty()) {
                LOG.info("error:Please add at least one seed");
                return;
            }

        }
        dbManager.open();

        if (needInject) {
            inject();
        }

        if (!forcedSeeds.isEmpty()) {
            injectForcedSeeds();
        }

        Generator generator = dbManager.getGenerator();
        if (maxExecuteCount >= 0) {
            generator.setMaxExecuteCount(maxExecuteCount);
        } else {
            generator.setMaxExecuteCount(Config.MAX_EXECUTE_COUNT);
        }
        generator.setTopN(topN);
        status = RUNNING;
        for (int i = 0; i < depth; i++) {
            if (status == STOPED) {
                break;
            }
            LOG.info("start depth " + (i + 1));
            long startTime = System.currentTimeMillis();
            fetcher = new Fetcher();
            fetcher.setDBManager(dbManager);
            fetcher.setExecutor(executor);
            fetcher.setThreads(threads);
            fetcher.setExecuteInterval(executeInterval);
            fetcher.fetchAll(generator);
            long endTime = System.currentTimeMillis();
            long costTime = (endTime - startTime) / 1000;
            int totalGenerate = generator.getTotalGenerate();

            LOG.info("depth " + (i + 1) + " finish: \n\ttotal urls:\t" + totalGenerate + "\n\ttotal time:\t" + costTime + " seconds");
            if (totalGenerate == 0) {
                break;
            }

        }
        dbManager.close();
    }

    /**
     * 停止爬虫
     */
    public void stop() {
        status = STOPED;
        fetcher.stop();
    }

    /**
     * 添加种子任务
     * @param datum 种子任务
     * @param force 如果添加的种子是已爬取的任务，当force为true时，会强制注入种子，当force为false时，会忽略该种子
     */    
    public void addSeed(CrawlDatum datum, boolean force) {
        if (force) {
            forcedSeeds.add(datum);
        } else {
            seeds.add(datum);
        }
    }
    
    /**
     * 等同于 addSeed(datum, false) 
     * @param datum 种子任务
     */
    public void addSeed(CrawlDatum datum) {
        addSeed(datum, false);
    }

    /**
     * 添加种子集合
     * @param datums 种子集合
     * @param force 如果添加的种子是已爬取的任务，当force为true时，会强制注入种子，当force为false时，会忽略该种子
     */
    public void addSeed(CrawlDatums datums, boolean force) {
        for (CrawlDatum datum : datums) {
            addSeed(datum, force);
        }
    }

    /**
     * 等同于 addSeed(datums,false)
     * @param datums 种子任务集合
     */
    public void addSeed(CrawlDatums datums) {
        addSeed(datums, false);
    }

    /**
     * 与addSeed(CrawlDatums datums, boolean force) 类似
     * @param links 种子URL集合
     * @param force 是否强制注入
     */
    public void addSeed(Links links, boolean force) {
        for (String url : links) {
            addSeed(url, force);
        }
    }

    /**
     * 与addSeed(CrawlDatums datums)类似
     * @param links 种子URL集合
     */
    public void addSeed(Links links) {
        addSeed(links, false);
    }

    /**
     * 与addSeed(CrawlDatum datum, boolean force)类似
     * @param url 种子URL
     * @param force 是否强制注入
     */
    public void addSeed(String url, boolean force) {
        CrawlDatum datum = new CrawlDatum(url);
        addSeed(datum, force);
    }

    /**
     * 与addSeed(CrawlDatum datum)类似
     * @param url 种子URL
     */
    public void addSeed(String url) {
        addSeed(url, false);
    }

    /**
     * 返回是否断点爬取
     * @return 是否断点爬取
     */
    public boolean isResumable() {
        return resumable;
    }

    /**
     * 设置是否断点爬取
     * @param resumable 是否断点爬取 
     */
    public void setResumable(boolean resumable) {
        this.resumable = resumable;
    }

    /**
     * 返回线程数
     * @return 线程数
     */
    public int getThreads() {
        return threads;
    }

    /**
     * 设置线程数
     * @param threads 线程数
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getMaxExecuteCount() {
        return maxExecuteCount;
    }

    /**
     * 设置每个爬取任务的最大执行次数，爬取或解析失败都会导致执行失败。
     * 当一个任务执行失败时，爬虫会在后面的迭代中重新执行该任务，
     * 当该任务执行失败的次数超过最大执行次数时，任务生成器会忽略该任务
     * @param maxExecuteCount 每个爬取任务的最大执行次数
     */
    public void setMaxExecuteCount(int maxExecuteCount) {
        this.maxExecuteCount = maxExecuteCount;
    }

    /**
     * 获取每个爬取任务的最大执行次数
     * @return 每个爬取任务的最大执行次数
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * 设置执行器
     * @param executor 执行器 
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }


    /**
     * 返回每次迭代爬取的网页数量上限
     * @return 每次迭代爬取的网页数量上限
     */
    public int getTopN() {
        return topN;
    }

    /**
     * 设置每次迭代爬取的网页数量上限
     * @param topN 每次迭代爬取的网页数量上限
     */
    public void setTopN(int topN) {
        this.topN = topN;
    }

    /**
     * 获取执行间隔
     * @return 执行间隔
     */
    public long getExecuteInterval() {
        return executeInterval;
    }

    /**
     * 设置执行间隔
     * @param executeInterval 执行间隔
     */
    public void setExecuteInterval(long executeInterval) {
        this.executeInterval = executeInterval;
    }

    /**
     * 返回任务管理器
     * @return 任务管理器
     */
    public DBManager getDBManager() {
        return dbManager;
    }

    /**
     * 设置任务管理器
     * @param dbManager 任务管理器 
     */
    public void setDBManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }


}
