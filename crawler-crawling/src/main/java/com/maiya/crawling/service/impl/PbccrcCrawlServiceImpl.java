package com.maiya.crawling.service.impl;

import com.maiya.common.enums.ReturnCodeEnum;
import com.maiya.common.exceptions.CustomBusinessException;
import com.maiya.common.util.EncryptCodeUtil;
import com.maiya.crawling.service.PbccrcCrawlService;
import com.maiya.crawling.util.DecideByClass;
import com.maiya.crawling.util.WebDriverUtil;
import com.maiya.dal.dao.myCrawler.PbccrcCrawlDao;
import com.maiya.dal.model.PbccrcSite;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;

import static com.maiya.crawling.util.CaptchaConverter.*;

/**
 * 央行征信信息抓取
 * Created by zhanglb on 2016/11/3.
 */
@Service
public class PbccrcCrawlServiceImpl implements PbccrcCrawlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PbccrcCrawlServiceImpl.class);

    private static String codeImgPath = System.getProperty("user.home");

    @Value("${des.encrypt.key}")
    private String maiyaDesKey;

    @Autowired
    private WebDriverUtil webDriverUtil;

    @Autowired
    private PbccrcCrawlDao pbccrcCrawlDao;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void doCrawl(String realName, String idCard, String userName, String password) throws CustomBusinessException {
        checkCrawlParams(realName, idCard, userName, password);

        PbccrcSite pbccrcSite = pbccrcCrawlDao.getPbccrcSite();
        if (pbccrcSite == null) {
            LOGGER.info("没有查询到需要抓取的授信网站信息");
            return;
        }
        //启动线程抓取数据
        taskExecutor.execute(new CrawlPbccrcInfoThread(realName, idCard, userName, password, pbccrcSite));
    }

    private class CrawlPbccrcInfoThread implements Runnable {

        @Override
        public void run() {
            RemoteWebDriver driver = null;//设置浏览器driver
            try {
                driver = webDriverUtil.getPlantomJsDriver(pbccrcSite.isUseProxy(), null);
                driver.get(pbccrcSite.getSiteUrl());
                //driver.get("https://ipcrs.pbccrc.org.cn/login.do?method=initLogin");
                //driver.switchTo().frame("headerFrame");

                WebElement xpathEle = driver.findElementByXPath("/html/body/div[1]/div[2]/a[1]");
                if (xpathEle == null) {
                    LOGGER.info("没有查询到登录按钮");
                    return;
                }

                xpathEle.click();
                Thread.sleep(randomInt());

                driver.findElement(DecideByClass.getBy(pbccrcSite.getUserNameElement(),
                        pbccrcSite.getUserNameAttr())).sendKeys(userName);//用户名输入框
                Thread.sleep(randomInt());
                driver.findElement(DecideByClass.getBy(pbccrcSite.getPasswordElement(),
                        pbccrcSite.getPasswordAttr())).sendKeys(EncryptCodeUtil.decrypt(password, maiyaDesKey));//密码输入框

                if (StringUtils.isNotEmpty(pbccrcSite.getCodeImgElement())) {//验证码图片
                    String pCodeImgUrl = driver.findElement(By.id(pbccrcSite.getCodeImgElement())).getAttribute("src");
                    saveCodeImage(pCodeImgUrl, idCard, pbccrcSite.getId(), driver);
                }
                if (StringUtils.isNotEmpty(pbccrcSite.getCodeElement())) {//验证码输入框
                    Thread.sleep(randomInt());
                    driver.findElement(By.id(pbccrcSite.getCodeElement())).sendKeys(parseCodeImg(idCard, pbccrcSite.getId()));
                }

                WebElement element = driver.findElement(DecideByClass.getBy(pbccrcSite.getLoginButtonElement(),
                        pbccrcSite.getLoginButtonAttr()));//提交按钮
                Actions builder = new Actions(driver);
                builder.moveToElement(element).click().build().perform();


                /*RestrictSiteEnum siteEnum = RestrictSiteEnum.getRestrictSiteEnum(pbccrcSite.getId());
                String pageData = getCrawlData(siteEnum, pbccrcSite, driver, idCard, realName);
                StringBuilder rowKey = new StringBuilder();
                rowKey.append(idCard).append("_").append(EncryptCodeUtil.encrypt(realName, maiyaDesKey))
                        .append("_").append(pbccrcSite.getId());
                if (retryTaskId != null) {//update 重试任务为成功状态
                    insertOrUpdateTask(new long[]{pbccrcSite.getId()}, realName, idCard, retryTaskId, true);
                }
                if (StringUtils.isEmpty(pageData)) {
                    LOGGER.info("抓取过程执行成功,但没有抓取到数据,siteUrl is {},realName is {},idCard is {}",
                            pbccrcSite.getSiteUrl(), realName, idCard);
                }
                taskExecutor.execute(new HbaseDataWorker(HBASE_CRAWL_TABLE, rowKey.toString(), HBASE__CRAWL_FAMILY,
                        new String[]{HBASE_QUALIFIERS_INFO}, pageData, hbaseUtil, mqMessageSender));*/
            } catch (Exception e) {
                LOGGER.error("抓取央行征信出现错误,error is {},realName is {},idCard is {}，userName is {}，password is {}", e,
                        realName, idCard, userName, password);
            } finally {

                if (driver != null) {
                    driver.quit();
                }

                File pCodeTempFile = new File(codeImgPath + "/tesseract/" + idCard + "_" + pbccrcSite.getId() + ".jpg");
                FileSystemUtils.deleteRecursively(pCodeTempFile);//删除保存的验证码临时图片
            }

        }

        private final Logger LOGGER = LoggerFactory.getLogger(CrawlPbccrcInfoThread.class);
        private String realName;//姓名
        private String idCard;//身份证
        private String userName;//登录用户名
        private String password;//密码
        private PbccrcSite pbccrcSite;

        CrawlPbccrcInfoThread(String realName, String idCard, String userName, String password, PbccrcSite pbccrcSite) {
            this.realName = realName;
            this.idCard = idCard;
            this.userName = userName;
            this.password = password;
            this.pbccrcSite = pbccrcSite;
        }
    }

    /**
     * 参数校验
     *
     * @param realName
     * @param idCard
     * @param userName
     * @param password
     */
    private void checkCrawlParams(String realName, String idCard, String userName, String password) {
        if (StringUtils.isEmpty(realName)) {
            throw new CustomBusinessException(ReturnCodeEnum.INVALID_PARAMETERS, "姓名不能为空");
        }
        if (StringUtils.isEmpty(idCard)) {
            throw new CustomBusinessException(ReturnCodeEnum.INVALID_PARAMETERS, "身份证不能为空");
        }
        if (StringUtils.isEmpty(userName)) {
            throw new CustomBusinessException(ReturnCodeEnum.INVALID_PARAMETERS, "登录名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            throw new CustomBusinessException(ReturnCodeEnum.INVALID_PARAMETERS, "密码不能为空");
        }
    }
}
