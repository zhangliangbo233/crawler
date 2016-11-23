package com.maiya.parse.model;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 用户的征信信息
 * Created by zhanglb on 16/9/2.
 */
public class UserCreditInfo {

    /**
     * 授信网站url
     */
    private String siteUrl;

    /**
     * 授信网站名
     */
    private String siteName;

    /**
     * 网站类型
     */
    private long siteType;

    /**
     * 用户授信网站的结果信息
     */
    private List<LinkedHashMap<String,String>> creditInfo;


    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public List<LinkedHashMap<String, String>> getCreditInfo() {
        return creditInfo;
    }

    public void setCreditInfo(List<LinkedHashMap<String, String>> creditInfo) {
        this.creditInfo = creditInfo;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public long getSiteType() {
        return siteType;
    }

    public void setSiteType(long siteType) {
        this.siteType = siteType;
    }
}
