package com.maiya.proxy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maiya.common.constants.MaiyaCrawlConstants;
import com.maiya.common.util.HttpUtil;
import com.maiya.dal.dao.myCrawler.CrawlProxyDao;
import com.maiya.dal.model.CrawlProxy;
import com.maiya.proxy.service.CrawlProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * proxy处理
 * Created by zhanglb on 16/9/5.
 */
@Service(value = "crawlProxyService")
public class CrawlProxyServiceImpl implements CrawlProxyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlProxyServiceImpl.class);

    @Autowired
    private CrawlProxyDao crawlProxyDao;


    @Value("${kuaidaili.request.url}")
    private String requestUrl;

    @Value("${telnet.connect.time.out}")
    private int connectTimeOut;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public CrawlProxy getAvailableProxy(String location) {

        List<CrawlProxy> proxys = crawlProxyDao.listProxy(location);
        if (proxys.isEmpty()) {
            LOGGER.info("从数据库中没有查询到代理,直接调用快代理的接口");
            return callOutSiteProxy(0);
        }
        CrawlProxy proxy = proxys.get(new Random().nextInt(proxys.size()));

        try {
            HttpUtil.telnetProxy(connectTimeOut, proxy.getProxyIp(), Integer.valueOf(proxy.getProxyPort()));
        } catch (Exception e) {
            LOGGER.error(proxy.getProxyIp() + ":" + proxy.getProxyPort() + " can not connect,delete unavailable proxy");
            crawlProxyDao.deleteUnavailable(proxy.getId());//删除不可用的代理
            getAvailableProxy(location);
        }
        return proxy;
    }

    /**
     * 调用快代理接口返回代理
     *@param call 调用的次数
     * @return
     */
    private CrawlProxy callOutSiteProxy(int call) {

        //调用快代理的次数最多5次
        call ++ ;
        if (call > MaiyaCrawlConstants.DEFAULT_CALL_KUAIDAILI_COUNT) {
            LOGGER.info("调用快代理的接口次数超过设置的最大{}次", MaiyaCrawlConstants.DEFAULT_CALL_KUAIDAILI_COUNT);
            return null;
        }

        //数据库没有查询到可用代理则调用接口
        String response = restTemplate.getForObject(requestUrl, String.class);
        JSONObject jsonInfo = JSON.parseObject(response);

        if (!MaiyaCrawlConstants.KUAIDAILI_PROXY_RETURN_CODE.equals(jsonInfo.getString("code"))
                || jsonInfo.getJSONObject("data") == null
                || jsonInfo.getJSONObject("data").getJSONArray("proxy_list") == null) {
            LOGGER.error("invoke kuaidaili interface error,code:{},msg:{}", jsonInfo.get("code"),
                    jsonInfo.get("msg"));
            return callOutSiteProxy(call);
        }

        JSONArray proxyJson = jsonInfo.getJSONObject("data").getJSONArray("proxy_list");

        //proxyInfo的格式 139.199.153.94:21340,广东省广州市
        Object aProxyJson = proxyJson.get(new Random().nextInt(proxyJson.size()));
        CrawlProxy proxy = new CrawlProxy();
        String[] proxyInfo = aProxyJson.toString().split(",");
        String ip = proxyInfo[0].split(":")[0];
        String port = proxyInfo[0].split(":")[1];
        proxy.setProxyIp(ip);
        proxy.setProxyPort(port);
        proxy.setLocation(proxyInfo[1]);
        proxy.setCreateTime(new Date());

        return proxy;
    }

}
