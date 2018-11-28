package org.cabbage.crawler.reaper.coordinator.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.coordinator.das.bean.ReaperTaskBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class AppContext {

	private static final Log LOGGER = LogFactory.getLog(AppContext.class);

	public static int ERROR_COUNT = 0;

	public static ApplicationContext APP_CONTEXT = new FileSystemXmlApplicationContext("conf/applicationContext.xml");

//	public static Map<Long, Long> LAST_ACTIVE_TIME_MAP = new ConcurrentHashMap<Long, Long>();
//
//	public static Map<Long, String> TASKID_2_HOST = new ConcurrentHashMap<Long, String>();
	
	public static Map<Long, ReaperTask> TASKID_2_TASK = new ConcurrentHashMap<Long, ReaperTask>();
	
	public static Map<Long, ReaperTaskBean> TASKID_2_TASK_BEAN = new ConcurrentHashMap<Long, ReaperTaskBean>();

//	public static Map<String, CopyOnWriteArraySet<Long>> HOST_2_TASKIDS = new ConcurrentHashMap<String, CopyOnWriteArraySet<Long>>();

}
