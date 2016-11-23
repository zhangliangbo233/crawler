package com.maiya.dal.dao.myCrawler;

import com.maiya.dal.model.CrawlRecord;

/**
 * Created by xiangdefei on 16/11/7.
 */
public interface CrawlRecordDao {

    public void insertRecord(CrawlRecord record);
    
    public CrawlRecord findRecordByUserIdentity(String userIdentity,String site);
    
    public void updateRecord(CrawlRecord record);
}
