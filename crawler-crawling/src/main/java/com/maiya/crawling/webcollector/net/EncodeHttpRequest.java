package com.maiya.crawling.webcollector.net;

import com.maiya.crawling.webcollector.model.CrawlDatum;
import com.maiya.crawling.webcollector.util.Config;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


@SuppressWarnings("Duplicates")
public class EncodeHttpRequest extends HttpRequest implements java.io.Serializable {

    static final long serialVersionUID = -7627629688361524111L;

    public static final Logger LOG = LoggerFactory.getLogger(EncodeHttpRequest.class);

    protected int MAX_REDIRECT = Config.MAX_REDIRECT;
    protected int MAX_RECEIVE_SIZE = Config.MAX_RECEIVE_SIZE;
    protected String method = Config.DEFAULT_HTTP_METHOD;
    protected boolean doinput = true;
    protected boolean dooutput = true;
    protected boolean followRedirects = false;
    protected int timeoutForConnect = Config.TIMEOUT_CONNECT;
    protected int timeoutForRead = Config.TIMEOUT_READ;
    protected byte[] outputData = null;
    Proxy proxy = null;

    protected Map<String, List<String>> headerMap = null;

    protected CrawlDatum crawlDatum = null;

    public EncodeHttpRequest(String url) throws Exception {
        super(url);
        this.crawlDatum = new CrawlDatum(url);
        setUserAgent(Config.DEFAULT_USER_AGENT);
    }

    public EncodeHttpRequest(String url, Proxy proxy) throws Exception {
        this(url);
        this.proxy = proxy;
    }

    public EncodeHttpRequest(CrawlDatum crawlDatum) throws Exception {
        super(crawlDatum);
        this.crawlDatum = crawlDatum;
        setUserAgent(Config.DEFAULT_USER_AGENT);
    }

    public EncodeHttpRequest(CrawlDatum crawlDatum, Proxy proxy) throws Exception {
        this(crawlDatum);
        this.proxy = proxy;
    }

    public HttpResponse getResponse() throws Exception {
        URL url = new URL(crawlDatum.getUrl());
        HttpResponse response = new HttpResponse(url);
        int code = -1;
        int maxRedirect = Math.max(0, MAX_REDIRECT);
        HttpURLConnection con = null;
        InputStream is = null;
        try {

            for (int redirect = 0; redirect <= maxRedirect; redirect++) {
                if (proxy == null) {
                    con = (HttpURLConnection) url.openConnection();
                } else {
                    //此处代理需要设置
                    con = (HttpURLConnection) url.openConnection(proxy);
                }

                config(con);

                //新增post请求参数
                if(outputData!=null){
                    OutputStream os=con.getOutputStream();
                    os.write(outputData);
                    os.close();
                }

                code = con.getResponseCode();
                /*只记录第一次返回的code*/
                if (redirect == 0) {
                    response.setCode(code);
                }

                if(code==HttpURLConnection.HTTP_NOT_FOUND){
                    response.setNotFound(true);
                    return response;
                }

                boolean needBreak = false;
                switch (code) {

                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        response.setRedirect(true);
                        if (redirect == MAX_REDIRECT) {
                            throw new Exception("redirect to much time");
                        }
                        String location = con.getHeaderField("Location");
                        if (location == null) {
                            throw new Exception("redirect with no location");
                        }
                        String originUrl = url.toString();
                        url = new URL(url, location);
                        response.setRealUrl(url);
                        LOG.info("redirect from " + originUrl + " to " + url.toString());
                        continue;
                    default:
                        needBreak = true;
                        break;
                }
                if (needBreak) {
                    break;
                }

            }

            is = con.getInputStream();
            String contentEncoding = con.getContentEncoding();
            if (contentEncoding != null && contentEncoding.equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            byte[] buf = new byte[2048];
            int read;
            int sum = 0;
            int maxsize = MAX_RECEIVE_SIZE;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((read = is.read(buf)) != -1) {
                if (maxsize > 0) {
                    sum = sum + read;

                    if (maxsize > 0 && sum > maxsize) {
                        read = maxsize - (sum - read);
                        bos.write(buf, 0, read);
                        break;
                    }
                }
                bos.write(buf, 0, read);
            }

            response.setContent(bos.toString("gbk").getBytes());
            response.setHeaders(con.getHeaderFields());
            bos.close();

            return response;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}
