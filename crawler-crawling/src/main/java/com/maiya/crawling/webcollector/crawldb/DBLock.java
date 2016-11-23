package com.maiya.crawling.webcollector.crawldb;


public interface DBLock {

    public void lock() throws Exception;

    public boolean isLocked() throws Exception;

    public void unlock() throws Exception;
}
