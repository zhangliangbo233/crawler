package com.maiya.proxy.service;

import com.maiya.dal.model.CrawlProxy;

/**
 * Created by zhanglb on 16/9/5.
 */
public interface CrawlProxyService {

    /**
     * 获取可用的代理
     * @param location 代理所在地区
     * @return
     */
    CrawlProxy getAvailableProxy(String location);

}
