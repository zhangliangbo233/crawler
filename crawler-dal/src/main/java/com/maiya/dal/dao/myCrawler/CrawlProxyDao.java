package com.maiya.dal.dao.myCrawler;


import com.maiya.dal.model.CrawlProxy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhanglb on 16/9/05.
 */
public interface CrawlProxyDao {

    /**
     * 查询代理信息
     * @param location
     */
    List<CrawlProxy> listProxy(@Param("location") String location);

    void delete();

    void deleteUnavailable(long id);

    void updateUnavailable(CrawlProxy proxy);

    void batchInsert(List<CrawlProxy> proxys);
}
