package com.maiya.crawling.util;

import com.maiya.crawling.webcollector.net.HttpRequest;
import com.maiya.crawling.webcollector.net.HttpResponse;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Random;

import static com.maiya.crawling.util.RestrictSiteCookie.concatCookie;

/**
 * 验证码转换处理
 * Created by zhanglb on 2016/9/24.
 */
public class CaptchaConverter {

    private static String codeImgPath = System.getProperty("user.home");

    /**
     * 保存验证码图片
     *
     * @throws Exception
     */
    public static void saveCodeImage(String codeImgUrl, String idCard, long id, WebDriver driver) throws Exception {
        String cookie = concatCookie(driver);
        HttpRequest request = new HttpRequest(codeImgUrl);
        request.setCookie(cookie);
        HttpResponse response = request.getResponse();
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(response.getContent());
            BufferedImage img = ImageIO.read(is);
            ImageIO.write(img, "jpg", new File(codeImgPath + "/tesseract/" + idCard + "_" + id + ".jpg"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * 解析验证码
     *
     * @param idCard
     * @param id
     * @return
     * @throws TesseractException
     */
    public static String parseCodeImg(String idCard, long id) throws TesseractException {
        File imageFile = new File(codeImgPath + "/tesseract/" + idCard + "_" + id + ".jpg");
        Tesseract tessreact = new Tesseract();
        tessreact.setDatapath(codeImgPath + "/tesseract/tessdata");//验证码语言库

        ImageIO.scanForPlugins();
        String pCode = tessreact.doOCR(imageFile);
        pCode = pCode.replaceAll("[\\t\\n\\r]", "");

        return pCode;
    }

    /**
     * 生成指定范围的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static int randomInt() {
        return randomInt(1000, 2000);
    }


}
