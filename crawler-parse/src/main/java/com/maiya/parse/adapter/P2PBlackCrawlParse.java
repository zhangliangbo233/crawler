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
 * p2p黑名单网站解析
 * Created by zhanglb on 16/9/19.
 */
public class P2PBlackCrawlParse extends AbstractCrawlParse {

    public Object splitData(String pageData, String realName, String idCard, RestrictSite site) {
        Document doc = Jsoup.parse(pageData);
        Elements elements = doc.getElementsByAttributeValue(site.getResultListAttr(),
                site.getResultListElement());
        if (elements.isEmpty()) {
            return null;
        }

        if (site.getResultLevel() > 0) {
            for (int i = 0; i < site.getResultLevel(); i++) {
                elements = elements.get(0).children();//获取结果列表
            }
        }

        List<Map<String, String>> creditInfo = new ArrayList<>();
        for (Element element : elements) {
            String data = element.text();
            if (StringUtils.isEmpty(data) || !data.contains(realName)) {
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
        Map<String, String> creditInfo = new HashMap<String, String>();
        String[] splitData = new String[2];

        rowDatas = data.split(" ");//切分每一行数据
        for (String cell : rowDatas) {//每一个单元数据
            if (!cell.contains("：") && !cell.contains(":")) {
                creditInfo.put("姓名", cell.trim());
                continue;
            }

            if (cell.contains("：")) {//同意split标准为英文状态的:
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
