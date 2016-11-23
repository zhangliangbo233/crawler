package com.maiya.dal.dao.myCrawler;


import com.maiya.dal.model.CrawlRetryTask;

import java.util.List;

/**
 * Created by zhanglb on 16/8/26.
 */
public interface CrawlRetryTaskDao {

    List<CrawlRetryTask> listRetryTasks();

    CrawlRetryTask findById(long id);

    void insert(CrawlRetryTask task);

    void update(CrawlRetryTask task);


}
