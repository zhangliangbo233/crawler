package com.maiya.dal.model;

/**
 * 授信站点信息
 * Created by zhanglb on 16/8/26.
 */
public class RestrictSite {

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
     * 是否需要登录
     */
    private boolean isNeedLogin;

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
     * 姓名输入框元素标识
     */
    private String realNameElement;

    /**
     * 元素标识(id,name,class等)
     */
    private String realNameAttr;

    /**
     * 身份证号输入框元素标识
     */
    private String idCardElement;

    private String idCardAttr;

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
    private String searchButtonElement;

    /**
     * 元素标识(id,name,class等)
     */
    private String searchButtonAttr;

    /**
     * 抓取查询结果时需要switch的ifame元素(有的网站需要driver switch到对应的iframe才可以获取到内容)
     */
    private String contentFrameElement;

    /**
     * 查询结果所在的元素
     */
    private String resultListElement;

    /**
     * 元素标识(id,name,class等)
     */
    private String resultListAttr;

    /**
     * 该站点是否需要抓取 1:是 0:否
     */
    private int isNeedCrawl;

    /**
     * 结果需要遍历的层次
     */
    private int resultLevel;

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

    public String getRealNameElement() {
        return realNameElement;
    }

    public void setRealNameElement(String realNameElement) {
        this.realNameElement = realNameElement;
    }

    public String getIdCardElement() {
        return idCardElement;
    }

    public void setIdCardElement(String idCardElement) {
        this.idCardElement = idCardElement;
    }

    public String getCodeElement() {
        return codeElement;
    }

    public void setCodeElement(String codeElement) {
        this.codeElement = codeElement;
    }

    public String getContentFrameElement() {
        return contentFrameElement;
    }

    public void setContentFrameElement(String contentFrameElement) {
        this.contentFrameElement = contentFrameElement;
    }

    public String getCodeImgElement() {
        return codeImgElement;
    }

    public void setCodeImgElement(String codeImgElement) {
        this.codeImgElement = codeImgElement;
    }

    public String getSearchButtonElement() {
        return searchButtonElement;
    }

    public void setSearchButtonElement(String searchButtonElement) {
        this.searchButtonElement = searchButtonElement;
    }

    public String getResultListElement() {
        return resultListElement;
    }

    public void setResultListElement(String resultListElement) {
        this.resultListElement = resultListElement;
    }

    public int getIsNeedCrawl() {
        return isNeedCrawl;
    }

    public void setIsNeedCrawl(int isNeedCrawl) {
        this.isNeedCrawl = isNeedCrawl;
    }

    public String getRealNameAttr() {
        return realNameAttr;
    }

    public void setRealNameAttr(String realNameAttr) {
        this.realNameAttr = realNameAttr;
    }

    public String getIdCardAttr() {
        return idCardAttr;
    }

    public void setIdCardAttr(String idCardAttr) {
        this.idCardAttr = idCardAttr;
    }

    public String getSearchButtonAttr() {
        return searchButtonAttr;
    }

    public void setSearchButtonAttr(String searchButtonAttr) {
        this.searchButtonAttr = searchButtonAttr;
    }

    public String getResultListAttr() {
        return resultListAttr;
    }

    public void setResultListAttr(String resultListAttr) {
        this.resultListAttr = resultListAttr;
    }

    public int getResultLevel() {
        return resultLevel;
    }

    public void setResultLevel(int resultLevel) {
        this.resultLevel = resultLevel;
    }

    public boolean getIsNeedLogin() {
        return isNeedLogin;
    }

    public void setIsNeedLogin(boolean isNeedLogin) {
        this.isNeedLogin = isNeedLogin;
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

    public String getSiteDataUrl() {
        return siteDataUrl;
    }

    public void setSiteDataUrl(String siteDataUrl) {
        this.siteDataUrl = siteDataUrl;
    }

    public boolean getIsUseProxy() {
        return isUseProxy;
    }

    public void setIsUseProxy(boolean isUseProxy) {
        this.isUseProxy = isUseProxy;
    }
}
