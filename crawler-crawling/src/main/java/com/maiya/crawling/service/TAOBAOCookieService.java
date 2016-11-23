package com.maiya.crawling.service;

import java.util.Set;

import com.maiya.crawling.util.CaptchaConverter;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maiya.common.util.MaskUtil;
import com.maiya.common.util.RandomUtil;
import com.maiya.crawling.util.DecideByClass;
import com.maiya.crawling.util.WebDriverUtil;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.dal.model.TAOBAOSite;

/**
 * 
 * 获取淘宝cookie
 */
@Component
public class TAOBAOCookieService {

	public static final Logger LOGGER = LoggerFactory.getLogger(TAOBAOCookieService.class);

	@Autowired
	private WebDriverUtil webDriverUtil;

	@Autowired
	private CrawlB2CRetryTaskService crawlB2CRetryTaskService;

	public String getCookie(TAOBAOSite site, String username, String password, String userIdentity, String userChannel,
			CrawlProxy crawlProxy, Long taskId) {
		RemoteWebDriver webDriver = null;
		try {

			webDriver = webDriverUtil.getDriver(site.isUseProxy(), crawlProxy);
			if (webDriver == null) {
				LOGGER.error("加载浏览器失败");

				if (taskId == null) {
					crawlB2CRetryTaskService.saveRetryTask(userIdentity, userChannel, "taobao", "加载浏览器失败");
				} else {
					crawlB2CRetryTaskService.updateRetryTask(taskId, false);
				}

				return null;
			}

			webDriver.get(site.getLoginUrl());

			// webDriver.findElement(By.id("J_Quick2Static")).click();

			// 输入用户名
			webDriver.findElement(DecideByClass.getBy(site.getUserNameElement(), site.getUserNameAttr()))
					.sendKeys(username);

			// 输入密码
			Thread.sleep(RandomUtil.randomInt(2000, 3000));
			webDriver.findElement(DecideByClass.getBy(site.getPasswordElement(), site.getPasswordAttr()))
					.sendKeys(password);

			// 点击登录按钮
			Thread.sleep(RandomUtil.randomInt(2000, 3000));
			webDriver.findElement(DecideByClass.getBy(site.getSubmitElement(), site.getSubmitAttr())).click();

			Thread.sleep(RandomUtil.randomInt(1000, 2000));

			if (webDriver.getCurrentUrl().startsWith(site.getLoginUrl())) {

				if (webDriver.findElementById("J_Message").findElement(By.className("error")).getText()
						.contains("你输入的密码和账户名不匹配")) {

					LOGGER.info("用户名或者密码错误");

					return null;

				} else if (webDriver.findElementById("J_Message").findElement(By.className("error")).getText()
						.contains("为了你的账户安全，请拖动滑块完成验证") && webDriver.findElementById("nc_1__scale_text") != null) {

					WebElement scaleText = webDriver.findElementById("nc_1__scale_text");//滑块
					Actions builder = new Actions(webDriver);
					Thread.sleep(CaptchaConverter.randomInt(2000,3000));
					builder.dragAndDropBy(scaleText,400,0).perform();

					WebElement text = webDriver.findElementByXPath("//div[@id='nc_1__scale_text']/span");
					String result = text.getText();
					LOGGER.info(result);



					LOGGER.info("出现滑块验证");

					if (taskId == null) {
						crawlB2CRetryTaskService.saveRetryTask(userIdentity, userChannel, "taobao", "出现滑块验证");

					} else {
						crawlB2CRetryTaskService.updateRetryTask(taskId, false);
					}
				}
				return null;

			}

			Set<Cookie> cookies = webDriver.manage().getCookies();

			StringBuilder sb = new StringBuilder();

			for (Cookie cookie : cookies) {
				sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
			}

			String cookie = sb.toString();

			return cookie;

		} catch (Exception e) {

			LOGGER.error("获取Cookie出现异常userIdentity:{},userName:{},e:{}", userIdentity, username, e);

			if (taskId == null) {
				crawlB2CRetryTaskService.saveRetryTask(userIdentity, userChannel, "taobao", e.getMessage());
			} else {
				crawlB2CRetryTaskService.updateRetryTask(taskId, false);
			}
			return null;
		} finally {
			webDriver.quit();

		}

	}
}
