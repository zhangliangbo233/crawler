package com.maiya.task.restrict;

import com.maiya.common.constants.MaiyaCrawlConstants;
import com.maiya.common.enums.CrawlTaskStatusEnum;
import com.maiya.common.util.RedisCacheUtil;
import com.maiya.crawling.service.RestrictCrawlService;
import com.maiya.dal.dao.credit.UserInfoDao;
import com.maiya.dal.dao.myCrawler.CrawlRetryTaskDao;
import com.maiya.dal.model.CrawlRetryTask;
import com.maiya.dal.model.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Created by zhanglb on 2016/9/28.
 */
@SuppressWarnings("unused")
@Service("restrictCrawlTaskService")
public class RestrictCrawlTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestrictCrawlTaskService.class);

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private CrawlRetryTaskDao crawlRetryTaskDao;

    @Autowired
    private RestrictCrawlService restrictCrawlService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Value("${crawl.fail.task.retry.num}")
    private int maxRetryNum;

    @Value("${history.crawl.month.num}")
    private int limitMonth;

    /**
     * 从某个日期开始查询,初始值
     */
    @Value("${history.offset.date}")
    private String offsetDate;

    /**
     * 已经执行的次数
     */
    private int exeNum;

    /**
     * 定时任务,抓取历史用户的授信信息
     */
    public void historyUserCrawl() {
        LOGGER.info("爬取历史用户数据 historyUserCrawl begin");
        initExeNum();
        List<UserInfo> userInfos = userInfoDao.listUser(offsetDate, exeNum * limitMonth, (exeNum + 1) * limitMonth);
        if (userInfos.isEmpty()) {
            LOGGER.info("没有查询到需要抓取的历史用户数据,offsetDate is {},exeNum is {}",offsetDate,exeNum);
            return;
        }

        for (UserInfo userInfo : userInfos) {
            if (StringUtils.isEmpty(userInfo.getRealName()) || StringUtils.isEmpty(userInfo.getIdCard())) {
                continue;
            }
            try {
                restrictCrawlService.doCrawl(userInfo.getRealName(), userInfo.getIdCard(), null, null);

            } catch (Exception e) {
                LOGGER.error("do historyUserCrawl error,realName{},idCard{},error info{}", userInfo.getRealName(),
                        userInfo.getIdCard(), e);
            }
        }
        exeNum = redisCacheUtil.incr(MaiyaCrawlConstants.HISTORY_EXECUTE_NUM,1);
        LOGGER.info("历史用户定时任务已经执行{}次,offsetDate is {} ", exeNum,offsetDate);
    }

    /**
     * 失败的任务重试
     */
    public void retryCrawl()  {
        LOGGER.info("重试失败的任务 retryCrawl begin");
        List<CrawlRetryTask> tasks = crawlRetryTaskDao.listRetryTasks();
        if (tasks.isEmpty()) {
            LOGGER.info("没有查询到需要重试的失败任务");
            return;
        }
        for (CrawlRetryTask task : tasks) {
            if (task.getRetryNum() >= maxRetryNum) {
                LOGGER.info("已经超过最大重试次数,不能再继续执行,taskId is {}", task.getId());
                task.setUpdateTime(new Date());
                task.setStatus(CrawlTaskStatusEnum.PASSED.getCode());
                crawlRetryTaskDao.update(task);
                continue;
            }
            try {
                restrictCrawlService.doCrawl(task.getRealName(), task.getIdCard(), task.getSiteId(), task.getId());
            } catch (UnsupportedEncodingException | InterruptedException e) {
                LOGGER.error("重试任务出错",e);
            }
        }

        LOGGER.info("retryCrawl end");
    }


    private void initExeNum() {
        Integer cacheNum = redisCacheUtil.get(MaiyaCrawlConstants.HISTORY_EXECUTE_NUM, Integer.class);
        if (cacheNum == null){
            redisCacheUtil.setNoExpire(MaiyaCrawlConstants.HISTORY_EXECUTE_NUM,0);
        }
        this.exeNum = cacheNum != null ? cacheNum : 0;
    }
}
