package com.maiya.parse.adapter;

import com.maiya.dal.model.RestrictSite;

/**
 * 公共解析类
 * Created by zhanglb on 16/9/19.
 */
public abstract class AbstractCrawlParse {

    /**
     * 拆分数据
     * @param pageData
     * @return
     */
    abstract Object splitData(String pageData,String realName, String idCard,RestrictSite site);
}
