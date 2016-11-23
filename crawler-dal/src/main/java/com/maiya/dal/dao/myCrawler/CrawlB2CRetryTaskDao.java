package com.maiya.dal.dao.myCrawler;


import java.util.List;

import com.maiya.dal.model.CrawlB2CRetryTask;

/**
 * 
 * @author xiangdf
 *
 */
public interface CrawlB2CRetryTaskDao {

    List<CrawlB2CRetryTask> listRetryTasks(String site);

    CrawlB2CRetryTask findById(long id);

    void insert(CrawlB2CRetryTask task);

    void update(CrawlB2CRetryTask task);


}
