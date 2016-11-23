package com.maiya.crawling.webcollector.crawldb;

import com.maiya.crawling.webcollector.model.CrawlDatum;
import com.maiya.crawling.webcollector.model.CrawlDatums;

/**
 * 爬取过程中，写入爬取历史、网页Content、解析信息的Writer
 *
 */
public interface SegmentWriter {

    public void initSegmentWriter() throws Exception;

    public void wrtieFetchSegment(CrawlDatum fetchDatum) throws Exception;

    public void writeRedirectSegment(CrawlDatum datum, String realUrl) throws Exception;

    public void wrtieParseSegment(CrawlDatums parseDatums) throws Exception;

    public void closeSegmentWriter() throws Exception;

}
