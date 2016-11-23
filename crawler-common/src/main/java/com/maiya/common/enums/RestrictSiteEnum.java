package com.maiya.common.enums;

/**
 * @author zhanglb
 * @date 2016年9月8日
 * @description 返回结果状态集合
 */
public enum RestrictSiteEnum {

    SHIXIN_COURT(1, "http://shixin.court.gov.cn", "法院失信信息", "com.maiya.crawling.crawler.ShiXinCourtCrawler",
            "com.maiya.parse.adapter.ShiXinCourtCrawlParse"),

    ZHIXING_COURT(2, "http://zhixing.court.gov.cn/search/", "法院被执行人信息", "com.maiya.crawling.crawler.ZhiXingCourtCrawler",
            "com.maiya.parse.adapter.ZhiXingCourtCrawlParse"),

    P2P_BLACK(4, "http://www.p2pblack.com/pageHome.html", "网贷信用黑名单", "com.maiya.crawling.crawler.P2PBlackCrawler",
            "com.maiya.parse.adapter.P2PBlackCrawlParse"),

    FAHAI_CC(5, "http://www.fahaicc.com", "法海风控", "com.maiya.crawling.crawler.FaHaiCrawler",
            "com.maiya.parse.adapter.FahaiCrawlParse");

    private final long siteType;

    /**
     * 状态码
     */
    private final String siteUrl;

    /**
     * 默认消息
     */
    private final String siteName;

    /**
     * 抓取类适配器
     */
    private final String crawlClassName;

    /**
     * 解析类适配器
     */
    private final String parseClassName;


    RestrictSiteEnum(long siteType, String siteUrl, String siteName,
                     String crawlClassName, String parseClassName) {
        this.siteType = siteType;
        this.siteUrl = siteUrl;
        this.siteName = siteName;
        this.crawlClassName = crawlClassName;
        this.parseClassName = parseClassName;
    }

    //通过value获取对应的枚举对象
    public static RestrictSiteEnum getRestrictSiteEnum(long value) {
        for (RestrictSiteEnum siteEnum : RestrictSiteEnum.values()) {
            if (value == siteEnum.getSiteType()) {
                return siteEnum;
            }
        }
        return SHIXIN_COURT;
    }

    public long getSiteType() {
        return siteType;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getCrawlClassName() {
        return crawlClassName;
    }

    public String getParseClassName() {
        return parseClassName;
    }
}
