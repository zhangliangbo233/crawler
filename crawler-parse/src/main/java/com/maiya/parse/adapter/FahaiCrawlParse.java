package com.maiya.parse.adapter;

import com.maiya.dal.model.RestrictSite;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 法海风控结果解析
 * Created by zhanglb on 16/9/19.
 */
public class FahaiCrawlParse extends AbstractCrawlParse {

    @Override
    public Object splitData(String pageData, String realName, String idCard, RestrictSite site) {

        //清苑区人民法院 开庭日期:2016/9/2   机构:清苑区人民法院   类别:开庭公告 庭依法公开审理张燕与 王帅
        Document doc = Jsoup.parse(pageData);
        Elements elements = doc.getElementsByTag(site.getResultListElement());
        if (elements.isEmpty()) {
            return null;
        }

        /*if (site.getResultLevel() > 0) {
            for (int i = 0; i < site.getResultLevel(); i++) {
                elements = elements.get(i).children();//获取结果列表
            }
        }*/

        List<Map<String, String>> creditInfo = new ArrayList<>();
        for (Element element : elements) {
            String data = element.children().text();
            if (StringUtils.isEmpty(data)) {
                continue;
            }
            creditInfo.add(split(data));
        }
        if (creditInfo.isEmpty()) {
            return null;
        }

        return creditInfo;
    }

    /**
     * split 每一行数据
     *
     * @param data
     * @return
     */
    private Map<String, String> split(String data) {
        String[] rowDatas;//切分每一行数据
        Map<String, String> creditInfo = new HashMap<>();
        String[] splitData = new String[2];

        rowDatas = data.split(" ");//切分每一行数据
        for (int i = 0; i < rowDatas.length; i++) {//每一个单元数据
            String cell = rowDatas[i];
            if (!cell.contains("：") && !cell.contains(":")) {
                if (i == 0) {
                    creditInfo.put("标题", cell.trim());
                }
                continue;
            }

            if (cell.contains("：")) {//统一split标准为英文状态的:
                cell = cell.replace("：", ":");
            }

            if (cell.split(":").length == 1) {
                splitData[0] = cell.split(":")[0];
                splitData[1] = "";
            } else {
                splitData = cell.split(":");
            }

            creditInfo.put(splitData[0], splitData[1]);
        }
        return creditInfo;
    }
}
