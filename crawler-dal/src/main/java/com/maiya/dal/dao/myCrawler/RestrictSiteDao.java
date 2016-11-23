package com.maiya.dal.dao.myCrawler;


import com.maiya.dal.model.RestrictSite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhanglb on 16/8/26.
 */
public interface RestrictSiteDao {

    /**
     * 查询需要抓取的授信站点信息
     * @param siteType
     */
    List<RestrictSite> listNeedCrawlSites(@Param("siteType") Long siteType);

    RestrictSite findById(long id);
}
