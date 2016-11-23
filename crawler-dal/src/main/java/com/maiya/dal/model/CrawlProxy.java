package com.maiya.dal.model;

import java.util.Date;

/**
 * proxy信息
 * Created by zhanglb on 16/9/5.
 */
public class CrawlProxy {

    private long id;

    /**
     * proxy ip
     */
    private String proxyIp;

    /**
     * proxy port
     */
    private String proxyPort;

    /**
     * 代理IP所在区域
     */
    private String location;

    /**
     * 匿名度信息
     */
    private String anonym;


    /**
     * 1:可用 0:不可用 (从接口获取到的默认为可用)
     */
    private int status;

    private Date createTime;

    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAnonym() {
        return anonym;
    }

    public void setAnonym(String anonym) {
        this.anonym = anonym;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return proxyIp+":"+proxyPort;
    }
}
