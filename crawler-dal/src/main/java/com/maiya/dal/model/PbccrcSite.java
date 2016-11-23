package com.maiya.dal.model;

/**
 * 授信站点信息
 * Created by zhanglb on 16/11/03.
 */
public class PbccrcSite {

    private long id;

    /**
     * 站点url
     */
    private String siteUrl;

    /**
     * 需要抓取数据的url
     */
    private String siteDataUrl;

    /**
     * 是否使用代理 1:使用 0:不使用
     */
    private boolean isUseProxy;

    /**
     * 用户名输入框
     */
    private String userNameElement;

    /**
     * 元素标识(id,name,class等)
     */
    private String userNameAttr;

    /**
     * 密码输入框
     */
    private String passwordElement;

    /**
     * 元素标识(id,name,class等)
     */
    private String passwordAttr;

    /**
     * 验证码输入框元素标识
     */
    private String codeElement;

    /**
     * 验证码图片元素
     */
    private String codeImgElement;

    /**
     * driver需要触发的按钮元素
     */
    private String loginButtonElement;

    /**
     * 元素标识(id,name,class等)
     */
    private String loginButtonAttr;

    /**
     * 该站点是否需要抓取 1:是 0:否
     */
    private int isNeedCrawl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getSiteDataUrl() {
        return siteDataUrl;
    }

    public void setSiteDataUrl(String siteDataUrl) {
        this.siteDataUrl = siteDataUrl;
    }

    public boolean isUseProxy() {
        return isUseProxy;
    }

    public void setUseProxy(boolean useProxy) {
        isUseProxy = useProxy;
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

    public String getCodeElement() {
        return codeElement;
    }

    public void setCodeElement(String codeElement) {
        this.codeElement = codeElement;
    }

    public String getCodeImgElement() {
        return codeImgElement;
    }

    public void setCodeImgElement(String codeImgElement) {
        this.codeImgElement = codeImgElement;
    }

    public String getLoginButtonElement() {
        return loginButtonElement;
    }

    public void setLoginButtonElement(String loginButtonElement) {
        this.loginButtonElement = loginButtonElement;
    }

    public String getLoginButtonAttr() {
        return loginButtonAttr;
    }

    public void setLoginButtonAttr(String loginButtonAttr) {
        this.loginButtonAttr = loginButtonAttr;
    }

    public int getIsNeedCrawl() {
        return isNeedCrawl;
    }

    public void setIsNeedCrawl(int isNeedCrawl) {
        this.isNeedCrawl = isNeedCrawl;
    }
}
