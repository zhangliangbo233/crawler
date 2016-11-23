package com.maiya.crawling.webcollector.crawldb;

import com.maiya.crawling.webcollector.model.CrawlDatum;
import com.maiya.crawling.webcollector.model.CrawlDatums;
import com.maiya.crawling.webcollector.model.Links;

public abstract class DBManager implements Injector, SegmentWriter, DBLock {

    public abstract boolean isDBExists();

    public abstract void clear() throws Exception;

    public abstract Generator getGenerator();

    public abstract void open() throws Exception;

    public abstract void close() throws Exception;

    public abstract void inject(CrawlDatum datum, boolean force) throws Exception;

    public abstract void inject(CrawlDatums datums, boolean force) throws Exception;

    public abstract void merge() throws Exception;

    public void inject(CrawlDatum datum) throws Exception {
        inject(datum, false);
    }

//    public void inject(CrawlDatums datums, boolean force) throws Exception {
//        for (CrawlDatum datum : datums) {
//            inject(datum, force);
//        }
//    }

    public void inject(CrawlDatums datums) throws Exception {
        inject(datums, false);
    }

    public void inject(Links links, boolean force) throws Exception {
        CrawlDatums datums = new CrawlDatums(links);
        inject(datums, force);
    }

    public void inject(Links links) throws Exception {
        inject(links, false);
    }

    public void inject(String url, boolean force) throws Exception {
        CrawlDatum datum = new CrawlDatum(url);
        inject(datum, force);
    }

    public void inject(String url) throws Exception {
        CrawlDatum datum = new CrawlDatum(url);
        inject(datum);
    }

}
