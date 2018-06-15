package org.cabbage.crawler.reaper.test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class Test {
	
	private static CookieStore cookieStore = new BasicCookieStore();

	private static CloseableHttpClient getInstanceClient() {
		CloseableHttpClient httpClient;
		StandardHttpRequestRetryHandler standardHandler = new StandardHttpRequestRetryHandler(5, true);
		HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException arg0, int retryTimes, HttpContext arg2) {
				if (arg0 instanceof UnknownHostException || arg0 instanceof ConnectTimeoutException
						|| !(arg0 instanceof SSLException) || arg0 instanceof NoHttpResponseException) {
					return true;
				}
				if (retryTimes > 5) {
					return false;
				}
				HttpClientContext clientContext = HttpClientContext.adapt(arg2);
				HttpRequest request = clientContext.getRequest();
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					// 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
					return true;
				}
				return false;
			}
		};
		HttpHost proxy = new HttpHost("127.0.0.1", 80);// 设置代理ip
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//		httpClient = HttpClients.custom().setRoutePlanner(routePlanner).setRetryHandler(handler)
//				.setConnectionTimeToLive(1, TimeUnit.DAYS).setDefaultCookieStore(cookieStore).build();
		httpClient = HttpClients.custom().setRetryHandler(handler)
				.setConnectionTimeToLive(1, TimeUnit.DAYS).setDefaultCookieStore(cookieStore).build();
		return httpClient;
	}
	
	static RequestConfig config = RequestConfig.custom().setConnectTimeout(6000).setSocketTimeout(6000)  
            .setCookieSpec(CookieSpecs.STANDARD_STRICT).build(); // 设置超时及cookie策略  
	
	
    public static String get(String url) {  
        HttpGet get = new HttpGet(url);  
        get.setConfig(config);
        HttpResponse response = null;  
        String html = null;  
        try {  
            response = getInstanceClient().execute(get);  
            int statusCode = response.getStatusLine().getStatusCode();// 连接代码  
            Header[] headers = response.getAllHeaders();  
            // 用于得到返回的文件头  
            for (Header header : headers) {  
                System.out.println(header);  
            }  
            html = new String(EntityUtils.toString(response.getEntity()).getBytes("iso-8859-1"), "UTF-8");  
            // 在后面参数输入网站的编码，一般为utf-8  
            // 返回的html代码,避免发生编码错误  
            System.out.println(html);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return html;  
    }  

	public static void main(String[] args) {
		String url = "http://v.ifeng.com/video_15539114.shtml";
		
		get(url);
	}

}
