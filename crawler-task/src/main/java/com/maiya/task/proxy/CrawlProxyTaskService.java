package com.maiya.task.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maiya.common.constants.MaiyaCrawlConstants;
import com.maiya.common.util.HttpUtil;
import com.maiya.dal.dao.myCrawler.CrawlProxyDao;
import com.maiya.dal.model.CrawlProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhanglb on 2016/9/28.
 */
@Service("crawlProxyTaskService")
public class CrawlProxyTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlProxyTaskService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CrawlProxyDao crawlProxyDao;

    @Value("${kuaidaili.request.url}")
    private String requestUrl;

    @Value("${telnet.connect.time.out}")
    private int connectTimeOut;

    /**
     * 定时更新数据库中的proxy信息
     */
    @SuppressWarnings("unused")
    public void doRenew() {
        LOGGER.info("doRenew Proxy begin");
        crawlProxyDao.delete();//删除不可用的代理信息

        //invoke代理接口
        String response = restTemplate.getForObject(requestUrl, String.class);

        JSONObject jsonInfo = JSON.parseObject(response);
        if (!MaiyaCrawlConstants.KUAIDAILI_PROXY_RETURN_CODE.equals(jsonInfo.getString("code"))) {
            LOGGER.error("invoke kuaidaili interface error,code:{},msg:{}", jsonInfo.get("code"),
                    jsonInfo.get("msg"));
            return;
        }
        JSONArray proxyJson = jsonInfo.getJSONObject("data").getJSONArray("proxy_list");
        List<CrawlProxy> proxys = new ArrayList<CrawlProxy>();
        CrawlProxy proxy;
        Date createTime = new Date();
        for (Object aProxyJson : proxyJson) {
            proxy = new CrawlProxy();
            //proxyInfo的格式 139.199.153.94:21340,广东省广州市
            String[] proxyInfo = aProxyJson.toString().split(",");
            String ip = proxyInfo[0].split(":")[0];
            String port = proxyInfo[0].split(":")[1];
            try {
                HttpUtil.telnetProxy(connectTimeOut, ip, Integer.parseInt(port));
            } catch (Exception e) {
                LOGGER.error("同步过来的代理不可用,ip is {},port is {}", ip, port);
                continue;
            }
            proxy.setProxyIp(ip);
            proxy.setProxyPort(port);
            proxy.setLocation(proxyInfo[1]);
            //proxy.setAnonym("高匿名");
            proxy.setCreateTime(createTime);

            proxys.add(proxy);
        }
        if (proxys.isEmpty()){
            LOGGER.info("没有同步到可用的代理 proxys.size is {}",proxys.size());
            return;
        }

        crawlProxyDao.batchInsert(proxys);

        LOGGER.info("doRenew Proxy success");
    }
}
