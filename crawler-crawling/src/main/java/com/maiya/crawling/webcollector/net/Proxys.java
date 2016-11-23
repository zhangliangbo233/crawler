
package com.maiya.crawling.webcollector.net;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Random;

import com.maiya.crawling.webcollector.util.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author zhukai
 * 代理帮助类
 */
public class Proxys extends ArrayList<Proxy>{
	private static final long serialVersionUID = 8683452581122892188L;
	
    public static final Logger LOG=LoggerFactory.getLogger(Proxys.class);

    public static Random random = new Random();
    
    public Proxy nextRandom(){
        int r=random.nextInt(this.size());
        return this.get(r);
    }
    
    public void addEmpty(){
        Proxy nullProxy=null;
        this.add(nullProxy);
    }

    public void add(String ip, int port) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
        this.add(proxy);
    }

    public void add(String proxyStr) throws Exception {
        try {
            String[] infos = proxyStr.split(":");
            String ip = infos[0];
            int port = Integer.valueOf(infos[1]);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            this.add(proxy);
        } catch (Exception ex) {
            LOG.info("Exception", ex);
        }

    }
    
    public void addD(String proxyStr) throws Exception {
        try {
            String[] infos = proxyStr.split(":");
            String ip = infos[0];
            int port = Integer.valueOf(infos[1]);

            Proxy proxy = new Proxy(Proxy.Type.DIRECT, new InetSocketAddress(ip, port));
            this.add(proxy);
        } catch (Exception ex) {
            LOG.info("Exception", ex);
        }

    }


    public void addAllFromFile(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#")||line.isEmpty()) {
                continue;
            } else {
                this.add(line);
            }
        }
        br.close();
        fis.close();
    }
    
    public void addAllFromFileD(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#")||line.isEmpty()) {
                continue;
            } else {
                this.addD(line);
            }
        }
        br.close();
        fis.close();
    }
    
    /**
     * @author zhukai
     * @param file
     * @param source
     * @throws Exception
     * 写入可以代理的IP
     */
    public static void insertFile(File file,String[] source) throws Exception {
    	file.delete();
		FileWriter fileWriter = new FileWriter(file,true);
		fileWriter.write("");
    	Document doc = null;
    	String result = "";
		for (String string : source) {
			try {
				//一般此处用一个服务端认可的cookie
				doc = Jsoup.connect(string).userAgent(Config.DEFAULT_USER_AGENT_OTHER)
						.cookie("_ga", "GA1.2.178963402.1467168087")
						.cookie("_gat", "1")
						.cookie("Hm_lpvt_7ed65b1cc4b810e9fd37959c9bb51b31", "1467249146")
						.cookie("Hm_lvt_7ed65b1cc4b810e9fd37959c9bb51b31", "1467168087,1467249136").timeout(5000)
						.referrer("http://www.kuaidaili.com/free/inha/1/")
						.get();
				Element listDiv = doc.getElementById("list");
				Elements trs = listDiv.select("table tbody tr");
				for (Element element : trs) {
					String ip = element.child(0).text();
					String port = element.child(1).text();
					result += (ip + ":" + port + "\r\n");
				}
			} catch (Exception e) {
				continue;
			}

		}

    	fileWriter.write(result);
		fileWriter.flush();
		fileWriter.close();
    }
    
    public static void main(String args[]) throws Exception{
    	Proxys.insertFile(new File("D:\\360Downloads\\kk.txt"),
				new String[] { "http://www.kuaidaili.com/free/inha/1/", "http://www.kuaidaili.com/free/inha/2/",
						"http://www.kuaidaili.com/free/inha/3/", "http://www.kuaidaili.com/free/inha/4/",
						"http://www.kuaidaili.com/free/inha/5/", "http://www.kuaidaili.com/free/inha/6/",
						"http://www.kuaidaili.com/free/inha/7/", "http://www.kuaidaili.com/free/inha/8/",
						"http://www.kuaidaili.com/free/inha/9/", "http://www.kuaidaili.com/free/inha/10/"});
    }
}
