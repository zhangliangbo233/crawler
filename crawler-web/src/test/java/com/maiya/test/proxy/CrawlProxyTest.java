package com.maiya.test.proxy;

import com.maiya.task.proxy.CrawlProxyTaskService;
import com.maiya.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhanglb on 16/9/5.
 */
public class CrawlProxyTest extends BaseTest {

    @Autowired
    private CrawlProxyTaskService crawlProxyTaskService;

    @Test
    public void testDoNew(){
        crawlProxyTaskService.doRenew();
    }

}
