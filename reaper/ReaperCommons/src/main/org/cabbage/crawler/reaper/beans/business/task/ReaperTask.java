package org.cabbage.crawler.reaper.beans.business.task;

import java.io.Serializable;
import java.util.Date;

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


	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new ReaperTask());
	}

	public void setLastWorkTime(Long timestamp) {
		// TODO Auto-generated method stub
		
	}
}
