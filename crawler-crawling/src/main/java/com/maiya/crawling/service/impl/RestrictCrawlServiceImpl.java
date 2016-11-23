package com.maiya.crawling.service.impl;

import com.alibaba.fastjson.JSON;
import com.maiya.common.enums.CrawlTaskStatusEnum;
import com.maiya.common.enums.RestrictSiteEnum;
import com.maiya.common.enums.ReturnCodeEnum;
import com.maiya.common.exceptions.CustomBusinessException;
import com.maiya.common.util.*;
import com.maiya.crawling.service.RestrictCrawlService;
import com.maiya.crawling.util.DecideByClass;
import com.maiya.crawling.util.WebDriverUtil;
import com.maiya.dal.dao.myCrawler.CrawlRetryTaskDao;
import com.maiya.dal.dao.myCrawler.RestrictSiteDao;
import com.maiya.dal.model.CrawlRetryTask;
import com.maiya.dal.model.RestrictSite;
import com.maiya.parse.model.UserCreditInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.maiya.common.constants.MaiyaCrawlConstants.*;
import static com.maiya.crawling.util.CaptchaConverter.*;

/**
 * 抓取授信网站信息
 * Created by zhanglb on 16/8/25.
 */
@Service("restrictCrawlService")
public class RestrictCrawlServiceImpl implements RestrictCrawlService, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestrictCrawlServiceImpl.class);

    private static String codeImgPath = System.getProperty("user.home");

    @Autowired
    private RestrictSiteDao restrictSiteDao;

    @Autowired
    private CrawlRetryTaskDao crawlRetryTaskDao;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private HbaseUtil hbaseUtil;

    @Autowired
    private MqMessageSender mqMessageSender;

    @Autowired
    private WebDriverUtil webDriverUtil;

    @Value("${des.encrypt.key}")
    private String maiyaDesKey;

    @Value("${fahai.login.user.name}")
    private String fahaiUserName;

    @Value("${fahai.login.password}")
    private String fahaiPassword;

    @Value("${xvfb.display.id}")
    private String xvfbDisplayId;

    @Value("${crawl.fail.task.retry.num}")
    private int maxRetryNum;

    @Value("${restrict.crawl.queue.size}")
    private int queueSize;

    @Value("${restrict.crawl.thread.size}")
    private int threadSize;

    private static CrawlTaskQueue<CrawlerProcessor> taskQueue;

    @Override
    public void afterPropertiesSet() throws Exception {
        taskQueue = new CrawlTaskQueue<>(queueSize);
    }

    @Override
    public void doCrawl(String realName, String idCard, Long siteType, Long retryTaskId)
            throws CustomBusinessException, UnsupportedEncodingException, InterruptedException {

        checkCrawlParams(realName, idCard);

        List<RestrictSite> restrictSites = restrictSiteDao.listNeedCrawlSites(siteType);
        if (restrictSites.isEmpty()) {
            LOGGER.info("没有查询到需要抓取的授信网站信息");
            return;
        }
        //启动线程抓取数据
        realName = URLDecoder.decode(realName, "UTF-8");
        taskQueue.add(new CrawlerProcessor(realName, idCard, restrictSites, retryTaskId));
    }


    /**
     * 爬虫的队列
     *
     * @param <T>
     */
    private class CrawlTaskQueue<T> {

        private final BlockingQueue<T> queue;

        private final Object lock = new Object();

        CrawlTaskQueue(int queueSize) {
            queue = new LinkedBlockingQueue<>(queueSize);
            execute();
        }

        public void add(T processor) {
            synchronized (lock) {
                try {
                    LOGGER.info("添加任务到队列中,队列中已有的长度为{}",queue.size()+1);
                    queue.put(processor);
                } catch (InterruptedException ignored) {
                }
            }
        }

        public void execute() {
            for (int i = 0; i < threadSize; i++) {
                taskExecutor.execute(new CrawlSiteInfoThread(queue, i));
            }
        }

    }

    /**
     * 获取队列里面的处理任务
     *
     * @param <T>
     */
    private class CrawlSiteInfoThread<T> implements Runnable {

        private final Logger LOGGER = LoggerFactory.getLogger(CrawlSiteInfoThread.class);

        private BlockingQueue<T> queue;
        private int threadIndex;

        CrawlSiteInfoThread(BlockingQueue<T> queue, int threadIndex) {
            this.queue = queue; // 持有队列来获取数据
            this.threadIndex = threadIndex; // 持有队列来获取数据
        }

        @Override
        public void run() {
            LOGGER.info("授信网站爬虫线程开始监听,线程号{},队列长度{}", threadIndex,queue.size());
            for (;;) {
                // 假如队列为空，则这个线程就把队列阻塞了，其它线程都需要等待。
                CrawlerProcessor processor;
                try {
                    processor = (CrawlerProcessor) queue.take();
                    LOGGER.info("获取到一个待处理的任务,开始处理,线程号为{},剩余队列长度{}",threadIndex,queue.size());
                    processor.doProcess();
                } catch (InterruptedException e) {
                    LOGGER.error("处理爬虫线程 {},爬取出现错误", e);
                }
            }
        }
    }

    /**
     * 爬虫爬取处理
     */
    private class CrawlerProcessor {

        private String realName;
        private String idCard;
        private List<RestrictSite> restrictSites;
        private Long retryTaskId;

        CrawlerProcessor(String realName, String idCard, List<RestrictSite> restrictSites, Long retryTaskId) {
            this.realName = realName;
            this.idCard = idCard;
            this.restrictSites = restrictSites;
            this.retryTaskId = retryTaskId;
        }

        void doProcess() {
            LOGGER.info("开始 doProcess start ...");
            RemoteWebDriver driver = null;//设置浏览器driver
            long[] siteIds = new long[restrictSites.size()];
            for (int i = 0; i < restrictSites.size(); i++) {
                siteIds[i] = restrictSites.get(i).getId();
            }
            try {
                for (RestrictSite site : restrictSites) {
                    if (driver == null) {
                        driver = webDriverUtil.getPlantomJsDriver(site.getIsUseProxy(), null);
                    }
                    driver.get(site.getSiteUrl());
                    try {
                        if (site.getIsNeedLogin()) {
                            String userNamePassword = getUserNamePassword();
                            String userName = userNamePassword.split(",")[0];
                            String password = userNamePassword.split(",")[1];
                            driver.findElement(DecideByClass.getBy(site.getUserNameElement(),
                                    site.getUserNameAttr())).sendKeys(userName);//用户名输入框
                            Thread.sleep(randomInt());//防止请求过快
                            driver.findElement(DecideByClass.getBy(site.getPasswordElement(),
                                    site.getPasswordAttr())).sendKeys(password);//密码输入框
                        }
                        if (StringUtils.isNotEmpty(site.getCodeImgElement())) {//验证码图片
                            String pCodeImgUrl = driver.findElement(By.id(site.getCodeImgElement())).getAttribute("src");
                            saveCodeImage(pCodeImgUrl, idCard, site.getId(), driver);
                        }
                        if (StringUtils.isNotEmpty(site.getCodeElement())) {//验证码输入框
                            Thread.sleep(randomInt());//防止请求过快
                            driver.findElement(By.id(site.getCodeElement())).sendKeys(parseCodeImg(idCard, site.getId()));
                        }
                        if (StringUtils.isNotEmpty(site.getRealNameElement())) {//姓名输入框
                            Thread.sleep(randomInt());//防止请求过快
                            driver.findElement(DecideByClass.getBy(site.getRealNameElement(), site.getRealNameAttr()))
                                    .sendKeys(realName);
                        }
                        if (StringUtils.isNotEmpty(site.getIdCardElement()) && StringUtils.isNotEmpty(idCard)) {//身份证输入框
                            driver.findElement(DecideByClass.getBy(site.getIdCardElement(), site.getIdCardAttr()))
                                    .sendKeys(idCard);
                        }

                        WebElement element = driver.findElement(DecideByClass.getBy(site.getSearchButtonElement(),
                                site.getSearchButtonAttr()));//提交按钮
                        Actions builder = new Actions(driver);
                        builder.moveToElement(element).click().build().perform();

                        RestrictSiteEnum siteEnum = RestrictSiteEnum.getRestrictSiteEnum(site.getId());
                        String pageData = getCrawlData(siteEnum, site, driver, idCard, realName);
                        StringBuilder rowKey = new StringBuilder();
                        rowKey.append(idCard).append("_").append(EncryptCodeUtil.encrypt(realName, maiyaDesKey))
                                .append("_").append(site.getId());
                        if (retryTaskId != null && StringUtils.isNotEmpty(pageData)) {//update 重试任务为成功状态
                            insertOrUpdateTask(new long[]{site.getId()}, realName, idCard, retryTaskId, true);
                        }

                        if (StringUtils.isNotEmpty(pageData)){
                            taskExecutor.execute(new HbaseDataWorker(HBASE_CRAWL_TABLE, rowKey.toString(), HBASE__CRAWL_FAMILY,
                                    new String[]{HBASE_QUALIFIERS_INFO}, pageData, hbaseUtil, mqMessageSender));
                        }
                    } catch (Throwable e) {
                        LOGGER.error("抓取出现错误,error is {},siteUrl is {},realName is {},idCard is {}", e,
                                site.getSiteUrl(), realName, idCard);
                        insertOrUpdateTask(new long[]{site.getId()}, realName, idCard, retryTaskId, false);
                    }
                }

            } catch (Throwable e) {
                LOGGER.error("获取Driver出现错误", e);
                insertOrUpdateTask(siteIds, realName, idCard, retryTaskId, false);
            } finally {
                LOGGER.info("关闭 close driver");
                if (driver != null) {
                    driver.quit();
                }
                for (long siteId : siteIds) {
                    File pCodeTempFile = new File(codeImgPath + "/tesseract/" + idCard + "_" + siteId + ".jpg");
                    FileSystemUtils.deleteRecursively(pCodeTempFile);//删除保存的验证码临时图片
                }
            }
        }
    }


    /**
     * 抓取失败的任务
     *
     * @param siteIds     抓取失败的网站id
     * @param realName
     * @param idCard
     * @param success     任务执行是否成功
     * @param retryTaskId 需要重试的任务id
     */
    private void insertOrUpdateTask(long[] siteIds, String realName, String idCard,
                                    Long retryTaskId, boolean success) {
        if (siteIds == null || siteIds.length == 0) {
            return;
        }
        LOGGER.info("insert or update 失败需要重试的任务信息,siteIds is {},realName is {},idCard is {},retryTaskId is {}",
                ArrayUtils.toString(siteIds), realName, idCard, retryTaskId);

        CrawlRetryTask task = null;

        for (long siteId : siteIds) {
            if (retryTaskId != null) {
                task = crawlRetryTaskDao.findById(retryTaskId);
            }
            if (task == null) {
                task = new CrawlRetryTask();
                task.setRealName(realName);
                task.setIdCard(idCard);
                task.setSiteId(siteId);
                task.setStatus(CrawlTaskStatusEnum.FAIL.getCode());
                task.setCreateTime(new Date());
                crawlRetryTaskDao.insert(task);
                return;
            }

            if (success) {
                task.setStatus(CrawlTaskStatusEnum.SUCCESS.getCode());
            }

            if (task.getRetryNum() >= maxRetryNum) {
                LOGGER.info("已经超过最大重试次数,修改状态为 passed ,taskId is {}", task.getId());
                task.setStatus(CrawlTaskStatusEnum.PASSED.getCode());
                // TODO: 2016/10/8 告警
            } else {
                task.setRetryNum(new AtomicInteger(task.getRetryNum()).incrementAndGet());
            }
            task.setUpdateTime(new Date());
            crawlRetryTaskDao.update(task);
        }
    }


    /**
     * 查询用户授信信息
     *
     * @param realName
     * @param idCard
     * @param siteType
     * @throws IOException
     */
    @Override
    public List<UserCreditInfo> obtainUserCreditInfo(String realName, String idCard, Long siteType) throws IOException {

        checkCrawlParams(realName, idCard);
        realName = URLDecoder.decode(realName, "UTF-8");

        List<RestrictSite> restrictSites = restrictSiteDao.listNeedCrawlSites(siteType);
        if (restrictSites.isEmpty()) {
            LOGGER.info("obtainUserCreditInfo interface,没有查询到需要抓取的授信网站信息");
            return null;
        }

        StringBuilder rowKey = new StringBuilder();
        List<UserCreditInfo> creditInfos = new ArrayList<UserCreditInfo>();
        UserCreditInfo creditInfo;
        for (RestrictSite site : restrictSites) {
            rowKey.delete(0, rowKey.length());
            rowKey.append(idCard).append("_").append(EncryptCodeUtil.encrypt(realName, maiyaDesKey))
                    .append("_").append(site.getId());

            //获取保存的原始数据
            String jsonData = hbaseUtil.getData(HBASE_CRAWL_TABLE, rowKey.toString(), HBASE_QUALIFIERS_ANALYSIS);
            if (StringUtils.isEmpty(jsonData)) {
                continue;
            }
            creditInfo = JSON.parseObject(jsonData, UserCreditInfo.class);
            creditInfos.add(creditInfo);
        }

        return creditInfos;
    }

    private void checkCrawlParams(String realName, String idCard) {
        if (StringUtils.isEmpty(realName)) {
            throw new CustomBusinessException(ReturnCodeEnum.INVALID_PARAMETERS, "姓名不能为空");
        }
        if (StringUtils.isEmpty(idCard)) {
            throw new CustomBusinessException(ReturnCodeEnum.INVALID_PARAMETERS, "身份证号不能为空");
        }
    }

    /**
     * 获取法海登录用户名密码
     *
     * @return
     * @throws Exception
     */
    private String getUserNamePassword() {
        if (StringUtils.isEmpty(fahaiUserName) || StringUtils.isEmpty(fahaiPassword)) {
            return "";
        }
        String[] userNames = fahaiUserName.split(",");
        String[] passwords = fahaiPassword.split(",");
        int index = new Random().nextInt(userNames.length);//随机索引值
        return userNames[index] + "," + passwords[index];
    }

    /**
     * 获取适配器抓取数据
     *
     * @param args
     */
    @SuppressWarnings("all")
    private String getCrawlData(RestrictSiteEnum siteEnum, Object... args) throws ClassNotFoundException {

        Class<?> adapterClass = Class.forName(siteEnum.getCrawlClassName());

        Method method = ReflectionUtils.findMethod(adapterClass, "startCrawl", null);
        if (method == null) {
            LOGGER.error("adapter class do not find invoke method,method name startCrawl," +
                    "siteUrl is {}", siteEnum.getSiteUrl());
            return null;
        }
        ReflectionUtils.makeAccessible(method);

        return (String) ReflectionUtils.invokeMethod(method, SpringContextHolder.getBean(adapterClass), args);
    }


}
