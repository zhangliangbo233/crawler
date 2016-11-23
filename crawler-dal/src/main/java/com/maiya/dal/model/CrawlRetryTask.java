package com.maiya.dal.model;

import java.util.Date;

/**
 * 需要重新爬取的任务
 * Created by zhanglb on 16/8/26.
 */
public class CrawlRetryTask {

    private long id;

    /**
     * 抓取失败的网站id
     */
    private long siteId;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 任务已经重试的次数
     */
    private int retryNum;

    /**
     * 任务状态 0:失败 1:成功 2:超过重试次数(默认5次,可配置)
     */
    private int status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }
}
