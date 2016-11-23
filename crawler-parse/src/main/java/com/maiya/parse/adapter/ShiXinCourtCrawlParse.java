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
public class ShiXinCourtCrawlParse extends AbstractCrawlParse {

    //失信网站抓取的JSON对应的标志,数组按顺序对应
    private static final String[] shiXinKey = {"iname", "sexy", "age", "cardNum", "courtName", "areaName", "gistId",
            "regDate", "caseCode", "gistUnit", "duty", "performance", "disruptTypeName", "publishDate"};
    private static final String[] shiXinValue = {"被执行人姓名/名称：", "性别：", "年龄：", "身份证号码/组织机构代码：",
            "执行法院：", "省份：", "执行依据文号：", "立案时间：", "案号：", "做出执行依据单位：", "生效法律文书确定的义务：",
            "被执行人的履行情况：", "失信被执行人行为具体情形：", "发布时间："};

    private static Map<String, String> shiXinMap;

    @Override
    public List<LinkedHashMap<String, String>> splitData(String pageData, String realName, String idCard, RestrictSite site) {
        List<Map<String, String>> parseData = JSON.parseObject(pageData, List.class);
        if (pageData.isEmpty()) {
            return null;
        }
        List<LinkedHashMap<String, String>> result = new ArrayList<>();
        Map<String, String> aData;
        LinkedHashMap<String, String> shuffle;//按照网站返回的顺序shuffle
        for (Map<String, String> aMap : parseData) {
            aData = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : aMap.entrySet()) {
                if (shiXinMap.get(entry.getKey()) != null) {
                    aData.put(shiXinMap.get(entry.getKey()), entry.getValue());
                }
            }
            //进行排序
            shuffle = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : shiXinMap.entrySet()) {
                shuffle.put(shiXinMap.get(entry.getKey()),aData.get(entry.getValue()));
            }

            result.add(shuffle);
        }

        return result;
    }

    /**
     * 初始化信息
     */
    static {
        shiXinMap = new LinkedHashMap<>();
        for (int i = 0; i < shiXinKey.length; i++) {
            shiXinMap.put(shiXinKey[i], shiXinValue[i]);
        }
    }


}
