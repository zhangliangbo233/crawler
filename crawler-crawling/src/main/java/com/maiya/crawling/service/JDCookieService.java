package com.maiya.crawling.service;

import static com.maiya.crawling.util.CaptchaConverter.parseCodeImg;
import static com.maiya.crawling.util.CaptchaConverter.saveCodeImage;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maiya.common.util.MaskUtil;
import com.maiya.common.util.RandomUtil;
import com.maiya.crawling.util.WebDriverUtil;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.dal.model.JDSite;

@Component
public class JDCookieService {

	public static final Logger LOGGER = LoggerFactory.getLogger(JDCookieService.class);

	private static final String USERNAME_OR_PWD_ERROR_TEXT = "账户名与密码不匹配，请重新输入";

	private static final String AUTHCODE_TEXT = "请输入验证码";

	private static final String AUTHCODE_ERROR_TEXT = "验证码不正确或验证码已过期";

	@Autowired
	private CrawlB2CRetryTaskService crawlB2CRetryTaskService;

	@Autowired
	private WebDriverUtil webDriverUtil;

	public String getCookie(JDSite site, String username, String password, String userIdentity, String userChannel,
			CrawlProxy crawlProxy, Long taskId) {

		StringBuilder sb = new StringBuilder();
		RemoteWebDriver driver = null;
		try {

			driver = webDriverUtil.getPlantomJsDriver(site.isUseProxy(), crawlProxy);
			if (driver == null) {
				LOGGER.error("加载浏览器失败");

				if (taskId == null) {
					crawlB2CRetryTaskService.saveRetryTask(userIdentity, userChannel, "jd", "加载浏览器失败");
				} else {
					crawlB2CRetryTaskService.updateRetryTask(taskId, false);
				}

				return null;
			}
			driver.get(site.getLoginUrl());

			Thread.sleep(RandomUtil.randomInt(1000, 1500));

			driver.findElementByClassName("login-tab-r").click();

			WebElement mobile = driver.findElementByCssSelector(site.getUserNameCss());
			mobile.sendKeys(username);

			Thread.sleep(RandomUtil.randomInt(3000, 4000));

			WebElement pass = driver.findElementByCssSelector(site.getPasswordCss());
			pass.sendKeys(password);

			Thread.sleep(RandomUtil.randomInt(3000, 4000));
			WebElement authcode = driver.findElementById("o-authcode");
			if (authcode != null && StringUtils.contains(authcode.getAttribute("style"), "block")) {
				LOGGER.info("登录出现验证码");
				String pCodeImgUrl = driver.findElement(By.id("JD_Verification1")).getAttribute("src");
				LOGGER.info("pCodeImgUrl:{}", pCodeImgUrl);
				saveCodeImage(pCodeImgUrl, userIdentity + userChannel, site.getId(), driver);
				String pCode = parseCodeImg(userIdentity + userChannel, site.getId());
				if (StringUtils.isEmpty(pCode)) {
					LOGGER.warn("识别验证码失败");
					return null;
				}
				LOGGER.info("识别后的验证码:{}", pCode);
				WebElement authCode = driver.findElementByCssSelector(site.getVerifyCodeCss());
				authCode.sendKeys(pCode);
				Thread.sleep(RandomUtil.randomInt(3000, 4000));
			}
			WebElement submit = driver.findElementByCssSelector(site.getSubmitCss());
			Thread.sleep(RandomUtil.randomInt(3000, 4000));
			submit.click();

			Thread.sleep(RandomUtil.randomInt(2000, 3000));

			if (driver.getCurrentUrl().startsWith(site.getLoginUrl())) {

				WebElement msgError = driver.findElementByClassName("msg-error");
				if (msgError != null) {
					LOGGER.info(msgError.getText());
					if (msgError.getText().contains(USERNAME_OR_PWD_ERROR_TEXT)) {
						LOGGER.info(msgError.getText());
						return null;
					} else if (msgError.getText().contains(AUTHCODE_ERROR_TEXT)) {
						LOGGER.info(msgError.getText());
					}

				}
				LOGGER.error("登录失败:", driver.getCurrentUrl());
				if (taskId == null) {
					crawlB2CRetryTaskService.saveRetryTask(userIdentity, userChannel, "jd", null);

				} else {
					crawlB2CRetryTaskService.updateRetryTask(taskId, false);
				}

			}

			Set<Cookie> cookieSet = driver.manage().getCookies();
			for (Cookie cookie : cookieSet) {
				sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
			}
			String result = sb.toString();
			// thor的cookie必须包含。
			if (result.indexOf("thor=") == -1) {
				return null;
			} else {
				return result;
			}

		} catch (Exception e) {
			LOGGER.error("获取Cookie出现异常userIdentity:{},userName:{},e:{}", userIdentity, username, e);
			if (taskId == null) {
				crawlB2CRetryTaskService.saveRetryTask(userIdentity, userChannel, "jd", e.getMessage());
			} else {
				crawlB2CRetryTaskService.updateRetryTask(taskId, false);
			}
			return null;
		} finally {
			if (driver != null) {
				driver.quit();
			}

		}
	}

}
