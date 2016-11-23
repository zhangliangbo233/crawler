package com.maiya.crawling.service;

import com.maiya.common.exceptions.CustomBusinessException;
import com.maiya.parse.model.UserCreditInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * p2p、失信等网站信息抓取
 * Created by zhanglb on 16/8/25.
 */
public interface RestrictCrawlService {

    /**
     * @param realName 用户姓名
     * @param idCard   身份证
     * @param siteType   授信网站类型
     * @param retryTaskId  重试任务的id
     */
    void doCrawl(String realName, String idCard,Long siteType,Long retryTaskId) throws CustomBusinessException, UnsupportedEncodingException, InterruptedException;

    List<UserCreditInfo> obtainUserCreditInfo(String realName, String idCard, Long siteType) throws IOException;
}
