package com.maiya.parse.restrict;

import com.maiya.parse.BaseTest;
import com.maiya.parse.service.ParseRestrictCrawlService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhanglb on 16/8/26.
 */
public class ParseCrawlServiceTest extends BaseTest {

    @Autowired
    private ParseRestrictCrawlService parseCrawlService;

    @Test
    public void testDoParse() throws Exception {
        parseCrawlService.doParse(null);
    }

}
