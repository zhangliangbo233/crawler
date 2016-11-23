package com.maiya.crawling.webcollector.fetcher;

import com.maiya.crawling.webcollector.model.CrawlDatums;
import com.maiya.crawling.webcollector.model.Page;


public interface Visitor {

    public abstract void visit(Page page, CrawlDatums next);

}
