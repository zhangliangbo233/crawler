package com.maiya.common.constants;

/**常量类
 * Created by zhanglb on 16/9/5.
 */
public class MaiyaCrawlConstants {

    private static final String REDIS_KEY_PREFIX = "myCrawler.";

    /**
     * 快代理返回的结果码
     */
    public static final String KUAIDAILI_PROXY_RETURN_CODE = "0";

    public  final static String HBASE_CRAWL_TABLE="my_crawler_crawling";

    /**
     * 列族
     */
    public final static String HBASE__CRAWL_FAMILY="userinfo";

    /**
     * 列
     */
    public final static String HBASE_QUALIFIERS_INFO="infomessage";

    /**
     *
     */
    public final static String HBASE_QUALIFIERS_ANALYSIS="analysismessage";

    /**
     * xvfb的虚拟显示的系统变量名
     */
    public final static String XVFB_DISPLAY="DISPLAY";

    /**
     * 历史用户抓取的定时任务执行的次数
     */
    public final static String HISTORY_EXECUTE_NUM = REDIS_KEY_PREFIX + "history.execute.num";

    /**
     * 默认的true
     */
    public static final String DEFAULT_YES_TRUE_FLAG = "1";

    /**
     * 默认的false
     */
    public static final String DEFAULT_NO_FALSE_FLAG = "0";

    /**
     * 如果查询数据库中没有可用的代理的时候，调用快代理的次数
     */
    public static final int DEFAULT_CALL_KUAIDAILI_COUNT = 5;

    /**
     * 调用实现或者执行网站，当出现验证码错误的时候，默认立即重试的次数
     */
    public static final int DEFAULT_CALL_COURT_COUNT = 8;


}
