package com.maiya.parse.adapter;

import com.alibaba.fastjson.JSON;
import com.maiya.dal.model.RestrictSite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanglb on 16/9/19.
 */
public class ZhiXingCourtCrawlParse extends AbstractCrawlParse {

    //失信网站抓取的JSON对应的标志,数组按顺序对应
    private static final String[] zhiXingKey = {"pname", "partyCardNum", "execCourtName", "caseCreateTime",
            "caseCode", "execMoney"};
    private static final String[] zhiXingValue = {"被执行人姓名/名称：", "身份证号码/组织机构代码：", "执行法院：", "立案时间：",
            "案号：", "执行标的："};

    private static Map<String, String> zhiXingMap;

    @Override
    public List<LinkedHashMap<String, String>> splitData(String pageData, String realName, String idCard, RestrictSite site) {
        List<Map<String, String>> parseData = JSON.parseObject(pageData, List.class);
        if (pageData.isEmpty()) {
            return null;
        }
        Map<String, String> aData;
        List<LinkedHashMap<String, String>> result = new ArrayList<>();
        LinkedHashMap<String, String> shuffle;//按照网站返回的顺序shuffle
        for (Map<String, String> aMap : parseData) {
            aData = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : aMap.entrySet()) {
                if (zhiXingMap.get(entry.getKey()) != null) {
                    aData.put(zhiXingMap.get(entry.getKey()), entry.getValue());
                }
            }
            //进行排序
            shuffle = new LinkedHashMap<String, String>();
            for (Map.Entry<String, String> entry : zhiXingMap.entrySet()) {
                shuffle.put(zhiXingMap.get(entry.getKey()), aData.get(entry.getValue()));
            }

            result.add(shuffle);
        }

        return result;
    }

    /**
     * 初始化信息
     */
    static {
        zhiXingMap = new LinkedHashMap<>();
        for (int i = 0; i < zhiXingKey.length; i++) {
            zhiXingMap.put(zhiXingKey[i], zhiXingValue[i]);
        }
    }


}
