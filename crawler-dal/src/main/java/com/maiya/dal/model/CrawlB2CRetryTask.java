package com.maiya.dal.model;

import java.util.Date;

/**
 * 
 * @author xiangdf
 *
 */
public class CrawlB2CRetryTask {

    private long id;

    /**
     * 抓取失败的网站
     */
    private String site;
    
    /**
     * 用户标识
     */
    private String userIdentity;
    
    /**
     * 用户渠道
     */
    private String userChannel;
    
    /**
     * 用户城市
     */
    private String userCity;

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
    
    
    private String reason;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public String getUserChannel() {
		return userChannel;
	}

	public void setUserChannel(String userChannel) {
		this.userChannel = userChannel;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getUserCity() {
		return userCity;
	}

	public void setUserCity(String userCity) {
		this.userCity = userCity;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}


}
