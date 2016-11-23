package com.maiya.dal.model;

/**
 * 从征信库中查询的用户信息
 * Created by zhanglb on 16/9/14.
 */
public class UserInfo {

    /**
     * 主键
     */
    private String uid;

    /**
     * 用户编号
     */
    private String userNo;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}
