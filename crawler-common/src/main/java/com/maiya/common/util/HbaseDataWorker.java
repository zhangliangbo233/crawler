package com.maiya.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 调用大数据保存数据接口
 * Created by zhanglb on 16/8/29.
 */
public class HbaseDataWorker implements Runnable{

    public static final Logger LOG = LoggerFactory.getLogger(HbaseDataWorker.class);

    private String table;

    /**
     *
     */
    private String rowKey;

    /**
     * 列族
     */
    private String columnFamily;

    /**
     * 列
     */
    private String[] column;

    /**
     * 要保存的数据
     */
    private String data;

    private HbaseUtil hbaseUtil;

    private MqMessageSender mqMessageSender;

    public HbaseDataWorker(String table, String rowKey, String columnFamily, String[] column,
                           String crawlResult, HbaseUtil hbaseUtil,MqMessageSender mqMessageSender) {
        this.table = table;
        this.rowKey = rowKey;
        this.columnFamily = columnFamily;
        this.column = column;
        this.data = crawlResult;
        this.hbaseUtil = hbaseUtil;
        this.mqMessageSender = mqMessageSender;
    }

    @Override
    public void run() {
        try {
            hbaseUtil.putData(table,rowKey,columnFamily,column,data);

            //调用mq发送消息
            if (mqMessageSender != null){
                mqMessageSender.send("maiya_restrictCrawlData_myCrawler",rowKey);
            }
        } catch (IOException e) {
            LOG.error("invoke hbase save data error:",e);
        }
    }
}
