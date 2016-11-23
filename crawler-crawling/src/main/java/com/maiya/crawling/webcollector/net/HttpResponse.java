package com.maiya.crawling.webcollector.net;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.maiya.crawling.webcollector.util.CharsetDetector;




public class HttpResponse {

    public static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HttpResponse.class);

    private URL url;
    private int code;
    private Map<String, List<String>> headers = null;
    private byte[] content = null;
    private boolean redirect = false;
    private boolean notFound=false;
    private String html = null;

    private URL realUrl = null;

    public HttpResponse(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getHtml(String charset) {
        if (content == null) {
            return null;
        }
        try {
            String html = new String(content, charset);
            return html;
        } catch (Exception ex) {
            LOG.info("Exception", ex);
            return null;
        }
    }

    public String getHtmlByCharsetDetect() {
        if (content == null) {
            return null;
        }
        String charset = CharsetDetector.guessEncoding(content);
        try {
            String html = new String(content, charset);
            return html;
        } catch (Exception ex) {
            LOG.info("Exception", ex);
            return null;
        }
    }

    public int getCode() {
        return code;
    }

    public boolean isNotFound() {
        return notFound;
    }

    public void setNotFound(boolean notFound) {
        this.notFound = notFound;
    }
    
    

    public List<String> getHeader(String name) {
        if (headers == null) {
            return null;
        }
        return headers.get(name);
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, List<String>> getHeaders() {
        if (headers == null) {
            return null;
        }
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public void setHeader(String key, List<String> values) {
        if (headers == null) {
            headers = new HashMap<String, List<String>>();
        }
        headers.put(key, values);
    }

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<String, List<String>>();
            addHeader(key, value);
        } else {
            List<String> header = getHeader(key);
            if (header != null) {
                header.add(value);
            } else {
                List<String> values = new ArrayList<String>();
                values.add(value);
                headers.put(key, values);
            }
        }

    }

    public String getContentType() {
        try {
            String contentType;
            List<String> contentTypeList = getHeader("Content-Type");
            if (contentTypeList == null) {
                contentType = null;
            } else {
                contentType = contentTypeList.get(0);
            }
            return contentType;
        } catch (Exception ex) {
            LOG.info("Exception", ex);
            return null;
        }
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public URL getRealUrl() {
        if (realUrl == null) {
            return url;
        }
        return realUrl;
    }

    public void setRealUrl(URL realUrl) {
        this.realUrl = realUrl;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

}
