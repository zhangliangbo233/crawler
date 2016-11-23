package com.maiya.dal.model;

/**
 * 
 * @author xiangdf
 *
 */
public class JDSite {

	
	private int id;
	
	private String loginUrl;
	
	private String userNameCss;
	
	private String passwordCss;
	
	private String verifyCodeCss;
	
	private String submitCss;
	
	
	private String seedUrl;
	
	/**
	 * 收货地址信息爬取URL
	 */
	private String addressSeedUrl;
	
    /**
     * 是否使用代理 1:使用 0:不使用
     */
    private boolean isUseProxy;
	

	public String getUserNameCss() {
		return userNameCss;
	}

	public void setUserNameCss(String userNameCss) {
		this.userNameCss = userNameCss;
	}

	public String getPasswordCss() {
		return passwordCss;
	}

	public void setPasswordCss(String passwordCss) {
		this.passwordCss = passwordCss;
	}

	public String getVerifyCodeCss() {
		return verifyCodeCss;
	}

	public void setVerifyCodeCss(String verifyCodeCss) {
		this.verifyCodeCss = verifyCodeCss;
	}

	public String getSubmitCss() {
		return submitCss;
	}

	public void setSubmitCss(String submitCss) {
		this.submitCss = submitCss;
	}


	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
}
