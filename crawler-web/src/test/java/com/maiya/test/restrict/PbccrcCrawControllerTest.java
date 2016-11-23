package com.maiya.test.restrict;

import com.maiya.common.util.EncryptCodeUtil;
import com.maiya.test.BaseTest;
import com.maiya.web.controller.restrict.PbccrcCrawlController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhanglb on 16/8/26.
 */
public class PbccrcCrawControllerTest extends BaseTest {

    @Autowired
    private PbccrcCrawlController pbccrcCrawlController;

    @Value("${des.encrypt.key}")
    private String maiyaDesKey;

    private String password;

    @Before
    public void init(){
        password = EncryptCodeUtil.encrypt("***",maiyaDesKey);
        //432B1BC1E34E9C7322062AB01C56A5DF
    }

    @Test
    public void testDoCrawl() throws Exception {

        MockMvc mock = MockMvcBuilders.standaloneSetup(pbccrcCrawlController).build();

        try {
            mock.perform(MockMvcRequestBuilders.post("/crawlPbccrcInfo.html")
                    .param("realName", "黄美娇")
                    .param("idCard", "511702198304257904")
                    .param("userName", "lanselixiang233")
                    .param("password", password));
            //.andExpect(jsonPath("$.stat", is("200")));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }







}
