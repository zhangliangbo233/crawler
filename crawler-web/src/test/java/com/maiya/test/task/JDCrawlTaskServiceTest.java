package com.maiya.test.task;

import com.maiya.task.restrict.JDCrawlTaskService;
import com.maiya.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xiangdefei on 16/10/28.
 */
public class JDCrawlTaskServiceTest  extends BaseTest{



    @Autowired
    private JDCrawlTaskService service;


    @Test
    public void test(){

        service.unAuthorCrawl();
    }

}
