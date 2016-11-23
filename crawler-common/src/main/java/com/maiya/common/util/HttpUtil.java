package com.maiya.common.util;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Created by zhanglb on 2016/9/29.
 */
public class HttpUtil {

    //保证client唯一性
    private static TelnetClient client = new TelnetClient();

    /**
     * telnet ip:port
     *
     * @param connectTimeout
     * @param ip
     * @param port
     */
    public static void telnetProxy(int connectTimeout, String ip, int port) throws IOException {
        try {
            client.setConnectTimeout(connectTimeout);
            client.connect(ip, port);
        } finally {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 创建httpclient
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static CloseableHttpClient acceptsUntrustedCertsHttpClient() throws KeyStoreException,
            NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder builder = HttpClientBuilder.create();

        // setup a Trust Strategy that allows all certificates.
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) {
                return true;
            }
        }).build();
        builder.setSSLContext(sslContext);

        // don't check Hostnames, either.
        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

        // here's the special part:
        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        //      -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // now, we create connection-manager using our Registry.
        //      -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connMgr.setMaxTotal(500);
        connMgr.setDefaultMaxPerRoute(200);
        builder.setConnectionManager(connMgr);

        // finally, build the HttpClient;
        //      -- done!

        return builder.build();
    }


}
