package org.cabbage.crawler.reaper.commons.network.httpclient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.commons.collections.MapUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient4.5 工具
 * 
 * @author wkshen
 *
 */
public class HttpClientUtils {

	private static final String HTTP = "http";

	private static final String HTTPS = "https";

	private static SSLConnectionSocketFactory SSL_CONNECTION_SOCKET_FACTORY = null;

	private static PoolingHttpClientConnectionManager POOLING_HTTPCLIENT_CONNECTION_MANAGER = null;

	private static SSLContextBuilder SSL_CONTEXT_BUILDER = null;

	private static RequestConfig REQUEST_CONFIG = null;

	static {
		try {
			SSL_CONTEXT_BUILDER = new SSLContextBuilder();
			// 全部信任 不做身份鉴定
			SSL_CONTEXT_BUILDER.loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					return true;
				}
			});
			SSL_CONNECTION_SOCKET_FACTORY = new SSLConnectionSocketFactory(SSL_CONTEXT_BUILDER.build(),
					new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" }, null, NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register(HTTP, new PlainConnectionSocketFactory()).register(HTTPS, SSL_CONNECTION_SOCKET_FACTORY)
					.build();
			POOLING_HTTPCLIENT_CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(registry);
			POOLING_HTTPCLIENT_CONNECTION_MANAGER.setMaxTotal(200);// max connection

			REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(6000).setSocketTimeout(6000)
					.setCookieSpec(CookieSpecs.STANDARD_STRICT).build(); // 设置超时及cookie策略
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * httpClient post请求
	 * 
	 * @param url
	 *            请求url
	 * @param header
	 *            头部信息
	 * @param param
	 *            请求参数 form提交适用
	 * @param entity
	 *            请求实体 json/xml提交适用
	 * @return 可能为空 需要处理
	 * @throws Exception
	 *
	 */
	public String post(String url, Map<String, String> header, Map<String, String> param, HttpEntity entity)
			throws Exception {
		String html = "";
		CloseableHttpClient httpClient = null;
		try {
			httpClient = getHttpClient(null, null);
			HttpPost httpPost = new HttpPost(url);
			// 设置头信息
			if (MapUtils.isNotEmpty(header)) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 设置请求参数
			if (MapUtils.isNotEmpty(param)) {
				List<NameValuePair> formparams = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : param.entrySet()) {
					// 给参数赋值
					formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
				httpPost.setEntity(urlEncodedFormEntity);
			}
			// 设置实体 优先级高
			if (entity != null) {
				httpPost.setEntity(entity);
			}
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = httpResponse.getEntity();
				html = EntityUtils.toString(resEntity);
			} else {
				html = readHttpResponse(httpResponse);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
		return html;
	}

	public String get(String url, HttpHost proxy, CookieStore cookieStore, String charset) {
		HttpGet get = new HttpGet(url);
		get.setConfig(REQUEST_CONFIG);
		HttpResponse response = null;
		String html = null;
		try {
			response = getHttpClient(null, null).execute(get);
			int statusCode = response.getStatusLine().getStatusCode();// 连接代码

			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				Header[] headers = response.getAllHeaders();
				// 用于得到返回的文件头
				for (Header header : headers) {
					System.out.println(header);
				}
				// html = EntityUtils.toString(resEntity);
				if (null == charset || charset.trim().length() == 0) {
					charset = "utf-8";
				}
				html = new String(EntityUtils.toString(resEntity).getBytes(charset), "UTF-8");
				// 在后面参数输入网站的编码，一般为utf-8
				// 返回的html代码,避免发生编码错误
				System.out.println(html);
			} else {
				html = readHttpResponse(response);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html;
	}

	private CloseableHttpClient getHttpClient(HttpHost proxy, CookieStore cookieStore) {

		CloseableHttpClient httpClient;

		HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException arg0, int retryTimes, HttpContext arg2) {
				if (arg0 instanceof UnknownHostException || arg0 instanceof ConnectTimeoutException
						|| !(arg0 instanceof SSLException) || arg0 instanceof NoHttpResponseException) {
					return false;
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

		HttpClientBuilder builder = HttpClients.custom().setSSLSocketFactory(SSL_CONNECTION_SOCKET_FACTORY)
				.setConnectionManager(POOLING_HTTPCLIENT_CONNECTION_MANAGER).setConnectionManagerShared(true)
				.setConnectionTimeToLive(1, TimeUnit.DAYS).setRetryHandler(retryHandler);

		if (null != proxy) {
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			builder = builder.setRoutePlanner(routePlanner);
		}

		if (null != cookieStore) {
			builder = builder.setDefaultCookieStore(cookieStore);
		} else {
			builder = builder.setDefaultCookieStore(new BasicCookieStore());
		}

		httpClient = builder.build();

		return httpClient;
	}

	private String readHttpResponse(HttpResponse httpResponse) throws ParseException, IOException {
		StringBuilder builder = new StringBuilder();
		// 获取响应消息实体
		HttpEntity entity = httpResponse.getEntity();
		// 响应状态
		builder.append("status:" + httpResponse.getStatusLine());
		builder.append("headers:");
		HeaderIterator iterator = httpResponse.headerIterator();
		while (iterator.hasNext()) {
			builder.append("\t" + iterator.next());
		}
		// 判断响应实体是否为空
		if (entity != null) {
			String responseString = EntityUtils.toString(entity);
			builder.append("response length:" + responseString.length());
			builder.append("response content:" + responseString.replace("\r\n", ""));
		}
		return builder.toString();
	}

	public static void main(String[] args) {
		String url = "http://v.ifeng1.com/video_15539114.shtml";
		HttpClientUtils client = new HttpClientUtils();
		client.get(url, null, null, "iso-8859-1");
	}
}
