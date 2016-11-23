package com.maiya.test.restrict;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maiya.parse.model.UserCreditInfo;
import com.maiya.test.BaseTest;
import com.maiya.web.controller.restrict.RestrictCrawlController;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhanglb on 16/8/26.
 */
public class RestrictCrawControllerTest extends BaseTest {

    @Autowired
    private RestrictCrawlController restrictCrawlController;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testDoCrawl() throws Exception {

        MockMvc mock = MockMvcBuilders.standaloneSetup(restrictCrawlController).build();

        try {
            mock.perform(MockMvcRequestBuilders.post("/crawlRestrictInfo.html")
                    //.param("realName", "潘文广"));
                    .param("realName", "黄美娇")
                    .param("idCard", "320826198607100816"));
            //.andExpect(jsonPath("$.stat", is("200")));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testObtainUserCreditInfo() throws Exception {
        MockMvc mock = MockMvcBuilders.standaloneSetup(restrictCrawlController).build();
        try {
            MvcResult result = mock.perform(MockMvcRequestBuilders.post("/obtainUserCreditInfo.html")
                    .param("realName", "黄美娇")
                    .param("idCard", "320826198607100816")).andReturn();
            System.out.println(result.getResponse().getContentAsString());
            //.andExpect(jsonPath("$.stat", is("200")));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testMap(){
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
        multiValueMap.add("realName", "李尚钟");//系统接入方标识
        multiValueMap.add("idCard", "532722197511040914");//系统接入方标识
        //multiValueMap.add("siteType", "2");//系统接入方标识


        /*String response = restTemplate.postForObject("http://192.168.1.164:8088/obtainUserCreditInfo.html",multiValueMap,String.class);

        System.out.println(response);*/


        LinkedHashMap response = restTemplate.postForObject("http://192.168.1.164:8088/obtainUserCreditInfo.html",multiValueMap,LinkedHashMap.class);

        List<LinkedHashMap> infos = (List<LinkedHashMap>) response.get("data");

        for (LinkedHashMap map : infos){

           List<LinkedHashMap> info = (List<LinkedHashMap>) map.get("creditInfo");
            System.out.println(info);

        }


        System.out.println(response);

    }

    public static void main(String[] args) {
        String codeImgPath = System.getProperty("user.home");


        File imageFile = new File(codeImgPath + "/tesseract/zlzp_code.gif");
        //File imageFile = new File(codeImgPath + "/tesseract/jd_code.jpeg");
        Tesseract tessreact = new Tesseract();
        tessreact.setDatapath(codeImgPath + "/tesseract/tessdata");//验证码语言库

        ImageIO.scanForPlugins();
        String pCode = null;
        try {
            pCode = tessreact.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        pCode = pCode.replaceAll("[\\t\\n\\r]", "");

        System.out.println(pCode);

    }

}
