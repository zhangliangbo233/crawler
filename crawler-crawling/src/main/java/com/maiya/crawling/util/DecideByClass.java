package com.maiya.crawling.util;

import org.openqa.selenium.By;

/**
 * 获取webelement时的By对象类型
 * Created by zhanglb on 16/8/29.
 */
public class DecideByClass {

    /**
     * 根据id定位element
     */
    public static final String ATTR_ID = "id";

    /**
     * 根据name定位element
     */
    public static final String ATTR_NAME= "name";

    /**
     * 根据class定位element
     */
    public static final String ATTR_CLASS= "class";

    /**
     * 根据xpath定位element
     */
    public static final String ATTR_XPATH= "xpath";//By.xpath("//input[@id='kw']")


    /**
     *
     * 获取By对象
     * @param eleSymbol
     * @param attrType
     * @return
     */
    public static By getBy(String eleSymbol, String attrType){

        switch (attrType) {
            case ATTR_ID:
                return new By.ById(eleSymbol);
            case ATTR_NAME:
                return new By.ByName(eleSymbol);
            case ATTR_CLASS:
                return new By.ByClassName(eleSymbol);
            case ATTR_XPATH:
                return new By.ByXPath(eleSymbol);
            default:
                return new By.ById(eleSymbol);
        }
    }
}
