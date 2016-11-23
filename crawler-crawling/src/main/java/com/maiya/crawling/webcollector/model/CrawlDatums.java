package com.maiya.crawling.webcollector.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 用于存储多个CrawlDatum的数据结构
 *
 */
public class CrawlDatums implements Iterable<CrawlDatum> {

    protected ArrayList<CrawlDatum> dataList = new ArrayList<CrawlDatum>();

    public CrawlDatums() {
    }

    public CrawlDatums(Links links) {
        add(links);
    }

    public CrawlDatums(CrawlDatums datums) {
        add(datums);
    }

    public CrawlDatums(Collection<CrawlDatum> datums) {
        for (CrawlDatum datum : datums) {
            this.add(datum);
        }
    }

    public CrawlDatums add(CrawlDatum datum) {
        dataList.add(datum);
        return this;
    }

    public CrawlDatums add(String url) {
        CrawlDatum datum = new CrawlDatum(url);
        return add(datum);
    }

    public CrawlDatums add(CrawlDatums datums) {
        dataList.addAll(datums.dataList);
        return this;
    }

    public CrawlDatums add(Links links) {
        for (String link : links) {
            add(link);
        }
        return this;
    }


    public CrawlDatums meta(String key, String value) {
        for (CrawlDatum datum : dataList) {
            datum.meta(key, value);
        }
        return this;
    }

    @Deprecated
    public CrawlDatums putMetaData(String key, String value) {
      return meta(key,value);
    }

    @Override
    public Iterator<CrawlDatum> iterator() {
        return dataList.iterator();
    }

    public CrawlDatum get(int index) {
        return dataList.get(index);
    }

    public int size() {
        return dataList.size();
    }

    public CrawlDatum remove(int index) {
        return dataList.remove(index);
    }

    public boolean remove(CrawlDatum datum) {
        return dataList.remove(datum);
    }

    public void clear() {
        dataList.clear();
    }

    public boolean isEmpty() {

        return dataList.isEmpty();
    }

    public int indexOf(CrawlDatum datum) {
        return dataList.indexOf(datum);
    }
    
     @Override
    public String toString() {
        return dataList.toString();
    }

}
