package com.maiya.crawling.webcollector.crawldb;

import com.maiya.crawling.webcollector.model.CrawlDatum;

/**
 * 抓取任务生成器
 *
 */
public interface Generator {

    public CrawlDatum next();
    
    public void open() throws Exception;

    public void setTopN(int topN);

    public void setMaxExecuteCount(int maxExecuteCount);

    public int getTotalGenerate();

    public void close() throws Exception;

}
