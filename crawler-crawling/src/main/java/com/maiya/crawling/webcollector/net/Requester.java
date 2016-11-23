package com.maiya.crawling.webcollector.net;

import com.maiya.crawling.webcollector.model.CrawlDatum;

public interface Requester {
     public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception;
}
