package org.cabbage.crawler.reaper.beans.business.task;

import java.io.Serializable;
import java.util.Random;

import org.dom4j.Document;

/**
 * Reaper任务定义
 * 
 * @author benny
 *
 */
public class ReaperTask implements Serializable{

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

	// 任务URL
	private String URL;

	// 处理流水ID
		private long processID = System.currentTimeMillis() * 1000 + (new Random().nextInt(10) * 100)
				+ (new Random().nextInt(10) * 10) + (new Random().nextInt(10));

		// 任务前置URL
		private String preURL;

		// 状态
		private Long status;

		// http状态值
		private int httpStatus;

		// 执行任务的机器名
		private String host;

		// 任务URL获取到的网页Document
		private Document document;

	/**
	 * 获取任务ID
	 * @return 任务ID
	 */
	public Long getID() {
		return ID;
	}

	/**
	 * 设置任务ID
	 * @param ID 任务ID
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
	public int getHttpStatus() {
		return httpStatus;
	}

	/**
	 * 
	 * @param httpStatus
	 */
	public void setHttpStatus(int httpStatus) {
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


	public void setLastWorkTime(Long timestamp) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new ReaperTask());
	}
}
