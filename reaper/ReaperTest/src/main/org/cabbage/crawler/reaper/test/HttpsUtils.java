package org.cabbage.crawler.reaper.test;

import org.apache.commons.collections.MapUtils;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpsUtils {
	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	private static SSLConnectionSocketFactory SSL_CONNECTION_SOCKET_FACTORY = null;
	private static PoolingHttpClientConnectionManager POOLING_HTTPCLIENT_CONNECTION_MANAGER = null;
	private static SSLContextBuilder SSL_CONTEXT_BUILDER = null;
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
	public static String post(String url, Map<String, String> header, Map<String, String> param, HttpEntity entity)
			throws Exception {
		String result = "";
		CloseableHttpClient httpClient = null;
		try {
			httpClient = getHttpClient();
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
				result = EntityUtils.toString(resEntity);
			} else {
				readHttpResponse(httpResponse);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
		return result;
	}

	public static CloseableHttpClient getHttpClient() throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(SSL_CONNECTION_SOCKET_FACTORY)
				.setConnectionManager(POOLING_HTTPCLIENT_CONNECTION_MANAGER).setConnectionManagerShared(true).build();
		return httpClient;
	}

	public static String readHttpResponse(HttpResponse httpResponse) throws ParseException, IOException {
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
}
