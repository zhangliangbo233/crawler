package com.maiya.crawling.webcollector.fetcher;

import com.maiya.crawling.webcollector.model.CrawlDatum;
import com.maiya.crawling.webcollector.model.CrawlDatums;

public interface Executor {
    public void execute(CrawlDatum datum, CrawlDatums next) throws Exception;
}
