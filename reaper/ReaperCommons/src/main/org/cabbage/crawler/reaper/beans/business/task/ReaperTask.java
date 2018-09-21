package org.cabbage.crawler.reaper.beans.business.task;

import java.io.Serializable;

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

	// 任务ID
	private long ID;

	// 任务URL
	private String URL;



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
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new ReaperTask());
	}
}
