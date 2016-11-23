package com.maiya.crawling.service;

import com.maiya.common.exceptions.CustomBusinessException;

/**
 * 央行征信信息抓取
 * Created by zhanglb on 16/11/03.
 */
public interface PbccrcCrawlService {

    /**
     * @param realName 姓名
     * @param idCard 身份证号
     * @param userName 登录用户名
     * @param password   密码
     */
    void doCrawl(String realName, String idCard, String userName, String password) throws CustomBusinessException;

}
