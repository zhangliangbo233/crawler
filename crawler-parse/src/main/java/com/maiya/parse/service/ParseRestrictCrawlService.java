package com.maiya.parse.service;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by zhanglb on 16/8/31.
 */
public interface ParseRestrictCrawlService extends MessageListener {

    void doParse(Message message);

    void onMessage(Message message);
}
