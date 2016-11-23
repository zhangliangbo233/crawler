package com.maiya.crawling.util;

import com.maiya.common.constants.MaiyaCrawlConstants;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.proxy.service.CrawlProxyService;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WebDriverUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(WebDriverUtil.class);

    @Autowired
    private CrawlProxyService crawlProxyService;

    @Value("${xvfb.display.id}")
    private String xvfbDisplayId;

    private static String homePath = System.getProperty("user.home");

    /**
     * 获取driver
     *
     * @param useProxy 是否使用代理
     * @param crawlProxy 代理IP所在区域
     * @return
     */
    @SuppressWarnings("Duplicates,unused")
    @Deprecated
    public RemoteWebDriver getDriver(boolean useProxy, CrawlProxy crawlProxy) {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        // 代理配置
        if (useProxy) {
        	String proxyIpAndPort;
            if (crawlProxy == null) {
                proxyIpAndPort = crawlProxyService.getAvailableProxy(null).toString();
            }else {
                proxyIpAndPort = crawlProxy.toString();
            }
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(proxyIpAndPort)
                    .setFtpProxy(proxyIpAndPort)
                    .setSslProxy(proxyIpAndPort);
            // 以下是为了避免localhost和selenium driver的也使用代理，务必要加
            capabilities.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
            capabilities.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
            System.setProperty("http.nonProxyHosts", "localhost");

            capabilities.setCapability(CapabilityType.PROXY, proxy);
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT,false);//是否截图
        }

        //设置虚拟的显示设备
        String xport = System.getProperty("lmportal.xvfb.id", xvfbDisplayId);
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.setEnvironmentProperty(MaiyaCrawlConstants.XVFB_DISPLAY, xport);

        RemoteWebDriver driver = new FirefoxDriver(firefoxBinary, new FirefoxProfile(), capabilities);
        //超时设置
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);

        return driver;
    }

    /**
     * 获取driver
     *
     * @param useProxy   是否使用代理
     * @param crawlProxy 代理IP所在区域
     * @return
     */
    @SuppressWarnings("Duplicates")
    public RemoteWebDriver getPlantomJsDriver(boolean useProxy, CrawlProxy crawlProxy) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        // 代理配置
        if (useProxy) {
            String proxyIpAndPort;
            if (crawlProxy == null) {
                proxyIpAndPort = crawlProxyService.getAvailableProxy(null).toString();
            }else {
                proxyIpAndPort = crawlProxy.toString();
            }
            LOGGER.info("proxy:{}", proxyIpAndPort);
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(proxyIpAndPort)
                    .setFtpProxy(proxyIpAndPort)
                    .setSslProxy(proxyIpAndPort);
            // 以下是为了避免localhost和selenium driver的也使用代理，务必要加
            capabilities.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
            capabilities.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
            System.setProperty("http.nonProxyHosts", "localhost");

            capabilities.setCapability(CapabilityType.PROXY, proxy);
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT,false);//是否截图
        }

        System.setProperty("phantomjs.binary.path", homePath + "/phantomjs/bin/phantomjs");
        RemoteWebDriver driver = new PhantomJSDriver(capabilities);
        //超时设置
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
        driver.manage().window().setSize(new Dimension(1024,768));

        return driver;
    }

}
