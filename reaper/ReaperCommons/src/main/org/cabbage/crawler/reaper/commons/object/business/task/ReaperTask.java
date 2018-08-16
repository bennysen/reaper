package org.cabbage.crawler.reaper.commons.object.business.task;

import java.util.Random;

import org.dom4j.Document;

/**
 * Reaper任务定义
 * 
 * @author benny
 *
 */
public class ReaperTask {

	// 任务ID
	private long ID;

	// 处理流水ID
	private long processID = System.currentTimeMillis() * 1000 + (new Random().nextInt(10) * 100)
			+ (new Random().nextInt(10) * 10) + (new Random().nextInt(10));

	// 任务URL
	private String URL;

	// 任务前置URL
	private String preURL;

	// 状态
	private int status;

	// http状态值
	private int httpStatus;

	// 任务URL获取到的网页Document
	private Document document;

	/**
	 * 获取任务ID
	 * @return 任务ID
	 */
	public long getID() {
		return ID;
	}

	/**
	 * 设置任务ID
	 * @param ID 任务ID
	 */
	public void setID(long ID) {
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
	public int getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
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

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new ReaperTask().processID);
	}
}