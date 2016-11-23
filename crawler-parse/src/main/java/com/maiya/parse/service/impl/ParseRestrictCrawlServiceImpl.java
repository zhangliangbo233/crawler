package com.maiya.parse.service.impl;

import com.alibaba.fastjson.JSON;
import com.maiya.common.annotations.MqConfig;
import com.maiya.common.constants.MaiyaCrawlConstants;
import com.maiya.common.enums.RestrictSiteEnum;
import com.maiya.common.util.EncryptCodeUtil;
import com.maiya.common.util.HbaseDataWorker;
import com.maiya.common.util.HbaseUtil;
import com.maiya.dal.dao.myCrawler.RestrictSiteDao;
import com.maiya.dal.model.RestrictSite;
import com.maiya.parse.model.UserCreditInfo;
import com.maiya.parse.service.ParseRestrictCrawlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 解析抓取的数据,mq监听
 * Created by zhanglb on 16/8/30.
 */
@Service
@MqConfig(destination = "parseRestrictCrawlDataQueue", destinationPhysicalName = "maiya_restrictCrawlData_myCrawler")
public class ParseRestrictCrawlServiceImpl implements ParseRestrictCrawlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseRestrictCrawlServiceImpl.class);

    @Autowired
    private HbaseUtil hbaseUtil;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private RestrictSiteDao restrictSiteDao;

    @Value("${des.encrypt.key}")
    private String maiyaDesKey;


    @Override
    public void onMessage(Message message) {
        doParse(message);
    }

    /**
     * 解析
     *
     * @param message
     */
    @Override
    public void doParse(Message message) {
        try {
            TextMessage content = (TextMessage) message;
            String rowKey = content.getText();

            long siteId = Long.parseLong(rowKey.split("_")[2]);//授信站点的主键id
            RestrictSite restrictSite = restrictSiteDao.findById(siteId);
            if (restrictSite == null) {
                return;
            }
            //获取保存的原始数据
            String pageData = hbaseUtil.getData(MaiyaCrawlConstants.HBASE_CRAWL_TABLE, rowKey,
                    MaiyaCrawlConstants.HBASE_QUALIFIERS_INFO);

            String idCard = rowKey.split("_")[0];//身份证号
            String realName = EncryptCodeUtil.decrypt(rowKey.split("_")[1], maiyaDesKey);//姓名

            UserCreditInfo creditInfo = getUserCreditInfo(restrictSite, pageData, realName, idCard);

            if (creditInfo == null) {
                LOGGER.info("no parse result,realName is {},idCard is {},siteUrl is {}", realName, idCard,
                        restrictSite.getSiteUrl());
                return;
            }
            //启用线程调用大数据接口
            taskExecutor.execute(new HbaseDataWorker(MaiyaCrawlConstants.HBASE_CRAWL_TABLE, rowKey,
                    MaiyaCrawlConstants.HBASE__CRAWL_FAMILY, new String[]{MaiyaCrawlConstants.HBASE_QUALIFIERS_ANALYSIS},
                    JSON.toJSONString(creditInfo), hbaseUtil, null));
        } catch (Exception e) {
            LOGGER.error("parse crawl data error:", e);
        }
    }

    private UserCreditInfo getUserCreditInfo(RestrictSite site, String pageData, String realName, String idCard)
            throws IOException, ClassNotFoundException {

        RestrictSiteEnum siteEnum = RestrictSiteEnum.getRestrictSiteEnum(site.getId());
        List<LinkedHashMap<String, String>> creditInfo = getAdapterParseData(pageData, realName, idCard, site, siteEnum);
        if (creditInfo == null || creditInfo.isEmpty()) {
            return null;
        }
        UserCreditInfo userCreditInfo = new UserCreditInfo();
        userCreditInfo.setSiteUrl(site.getSiteUrl());
        userCreditInfo.setCreditInfo(creditInfo);
        userCreditInfo.setSiteType(site.getId());
        userCreditInfo.setSiteName(siteEnum.getSiteName());

        return userCreditInfo;
    }


    /**
     * 获取适配器解析的结果数据
     *
     * @param data
     * @param site
     * @param siteEnum
     */
    @SuppressWarnings("all")
    private List<LinkedHashMap<String, String>> getAdapterParseData(String data, String realName,
                    String idCard, RestrictSite site, RestrictSiteEnum siteEnum) throws ClassNotFoundException {

        Class<?> adapterClass = Class.forName(siteEnum.getParseClassName());

        Method method = ReflectionUtils.findMethod(adapterClass, "splitData", String.class, String.class,
                String.class, site.getClass());

        if (method == null) {
            LOGGER.error("adapter class do not find invoke method,method name splitData");
            return null;
        }
        ReflectionUtils.makeAccessible(method);

        return (List<LinkedHashMap<String, String>>) ReflectionUtils.invokeMethod(method, BeanUtils.instantiateClass(adapterClass),
                data, realName, idCard, site);

    }
}
