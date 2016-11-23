package com.maiya.dal.model;

public class TAOBAOSite {

	
    private String loginUrl;

	private String userNameElement;
	
	private String userNameAttr;
	
	private String passwordElement;
	
	private String passwordAttr;
	
	private String submitElement;
	
	private String submitAttr;
	
	private String seedUrl;
	
	/**
	 * 收货地址信息爬取URL
	 */
	private String addressSeedUrl;
	
	
    /**
     * 是否使用代理 1:使用 0:不使用
     */
    private boolean isUseProxy;
	
	
	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getUserNameElement() {
		return userNameElement;
	}

	public void setUserNameElement(String userNameElement) {
		this.userNameElement = userNameElement;
	}

	public String getUserNameAttr() {
		return userNameAttr;
	}

	public void setUserNameAttr(String userNameAttr) {
		this.userNameAttr = userNameAttr;
	}

	public String getPasswordElement() {
		return passwordElement;
	}

	public void setPasswordElement(String passwordElement) {
		this.passwordElement = passwordElement;
	}

	public String getPasswordAttr() {
		return passwordAttr;
	}

	public void setPasswordAttr(String passwordAttr) {
		this.passwordAttr = passwordAttr;
	}

	public String getSubmitElement() {
		return submitElement;
	}

	public void setSubmitElement(String submitElement) {
		this.submitElement = submitElement;
	}

	public String getSubmitAttr() {
		return submitAttr;
	}

	public void setSubmitAttr(String submitAttr) {
		this.submitAttr = submitAttr;
	}

	public String getSeedUrl() {
		return seedUrl;
	}

	public void setSeedUrl(String seedUrl) {
		this.seedUrl = seedUrl;
	}

	public boolean isUseProxy() {
		return isUseProxy;
	}

	public void setUseProxy(boolean isUseProxy) {
		this.isUseProxy = isUseProxy;
	}

	public String getAddressSeedUrl() {
		return addressSeedUrl;
	}

	public void setAddressSeedUrl(String addressSeedUrl) {
		this.addressSeedUrl = addressSeedUrl;
	}

}
