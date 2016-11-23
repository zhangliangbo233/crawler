package com.maiya.dal.model;

import java.util.Date;

/**
 * Created by xiangdefei on 16/11/7.
 */
public class CrawlRecord {

    private  int id;

    private  String site;

    private  int loginFlag;

    private  int crawlOrderFlag;

    private int crawlAddressFlag;

    private String userIdentity;

    private String userName;

    private Date createTime;

    private Date updateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public int getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(int loginFlag) {
		this.loginFlag = loginFlag;
	}

	public int getCrawlOrderFlag() {
		return crawlOrderFlag;
	}

	public void setCrawlOrderFlag(int crawlOrderFlag) {
		this.crawlOrderFlag = crawlOrderFlag;
	}

	public int getCrawlAddressFlag() {
		return crawlAddressFlag;
	}

	public void setCrawlAddressFlag(int crawlAddressFlag) {
		this.crawlAddressFlag = crawlAddressFlag;
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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




}
