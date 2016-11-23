package com.maiya.crawling.webcollector.crawldb;

import com.maiya.crawling.webcollector.model.CrawlDatum;


public interface Injector {
     public void inject(CrawlDatum datum) throws Exception;
}
