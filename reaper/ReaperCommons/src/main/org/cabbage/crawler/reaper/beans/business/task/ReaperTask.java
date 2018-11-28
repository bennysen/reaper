package org.cabbage.crawler.reaper.beans.business.task;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.dom4j.Document;

/**
 * Reaper任务定义
 * 
 * @author benny
 *
 */
public class ReaperTask implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7399746645141946120L;

	public static final Long INIT = 0l;
	public static final Long RUNNING = 1l;
	public static final Long STOP = 2l;
	public static final Long DISABLE = 3l;
	public static final Long FINISH = 4l;

	// 任务ID
	private Long ID;

	// 站点名称
	private String siteName;

	// 任务URL
	private String URL;

	private Long siteType;

	private Long countryID;

	private Long domainID;

	private Long domainType;

	private Long proxyType;

	private Long proxyIP;

	private Long proxyPort;

	private Long lastWorkTime;
	
	private List<Long> scriptIDs;
	
	// 处理流水ID
	private Long processID = System.currentTimeMillis() * 1000 + (new Random().nextInt(10) * 100)
			+ (new Random().nextInt(10) * 10) + (new Random().nextInt(10));

	// 任务前置URL
	private String preURL;

	// 状态
	private Long status;

	// http状态值
	private Long httpStatus;

	// 执行任务的机器名
	private String host;

	private String charset = "utf-8";

	private HttpHost proxy;

	private CookieStore cookieStore;

	// 任务URL获取到的网页Document
	private Document document;

	/**
	 * 获取任务ID
	 * 
	 * @return 任务ID
	 */
	public Long getID() {
		return ID;
	}

	/**
	 * 设置任务ID
	 * 
	 * @param ID
	 *            任务ID
	 */
	public void setID(Long ID) {
		this.ID = ID;
	}

	/**
	 * 
	 * @return
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * 
	 * @param URL
	 */
	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * 
	 * @return
	 */
	public String getPreURL() {
		if (null == preURL) {
			return URL;
		}
		return preURL;
	}

	/**
	 * 
	 * @param preURL
	 */
	public void setPreURL(String preURL) {
		this.preURL = preURL;
	}

	/**
	 * 
	 * @return
	 */
	public Long getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(Long status) {
		this.status = status;
	}

	/**
	 * 
	 * @return
	 */
	public Long getHttpStatus() {
		return httpStatus;
	}

	/**
	 * 
	 * @param httpStatus
	 */
	public void setHttpStatus(Long httpStatus) {
		this.httpStatus = httpStatus;
	}

	/**
	 * 
	 * @return
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * 
	 * @param document
	 */
	public void setDocument(Document document) {
		this.document = document;
	}

	/**
	 * 
	 * @return
	 */
	public long getProcessID() {
		return processID;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public HttpHost getProxy() {
		return proxy;
	}

	public void setProxy(HttpHost proxy) {
		this.proxy = proxy;
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public boolean isInvalid() {
		boolean isInvalid = false;
		if (null == this.URL || this.URL.trim().length() == 0) {
			isInvalid = true;
		}
		return isInvalid;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Long getSiteType() {
		return siteType;
	}

	public void setSiteType(Long siteType) {
		this.siteType = siteType;
	}

	public Long getCountryID() {
		return countryID;
	}

	public void setCountryID(Long countryID) {
		this.countryID = countryID;
	}

	public Long getDomainID() {
		return domainID;
	}

	public void setDomainID(Long domainID) {
		this.domainID = domainID;
	}

	public Long getDomainType() {
		return domainType;
	}

	public void setDomainType(Long domainType) {
		this.domainType = domainType;
	}

	public Long getProxyIP() {
		return proxyIP;
	}

	public void setProxyIP(Long proxyIP) {
		this.proxyIP = proxyIP;
	}

	public Long getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Long proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setProcessID(Long processID) {
		this.processID = processID;
	}

	public Long getProxyType() {
		return proxyType;
	}

	public void setProxyType(Long proxyType) {
		this.proxyType = proxyType;
	}

	public Long getLastWorkTime() {
		return lastWorkTime;
	}

	public void setLastWorkTime(Long lastWorkTime) {
		this.lastWorkTime = lastWorkTime;
	}

	public List<Long> getScriptIDs() {
		return scriptIDs;
	}

	public void setScriptIDs(List<Long> scriptIDs) {
		this.scriptIDs = scriptIDs;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new ReaperTask());
	}
}
