package org.cabbage.crawler.reaper.worker.context;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;

public class AppContext {

	private static final Log LOGGER = LogFactory.getLog(AppContext.class);

	private static int ERROR_COUNT = 0;

	private static ThreadLocal<ReaperTask> CURRENT_TASK = new ThreadLocal<ReaperTask>();

	/**
	 * Manager中处理的任务Map<Long,Date>
	 */
	private static Map<Long, Date> LAST_ACTIVE_TIME_MAP = new ConcurrentHashMap<Long, Date>();

	private static Map<Long, ReaperTask> TASKS = new ConcurrentHashMap<Long, ReaperTask>();


	public synchronized static ReaperTask getTask(Long taskID) {
		return TASKS.get(taskID);
	}

	public synchronized static void putTask(ReaperTask task) {
		if (null != task && null != task.getID()) {
			TASKS.put(task.getID(), task);
			CURRENT_TASK.set(task);
		}
	}

	public synchronized static void removeTask(ReaperTask task) {
		if (null == task) {
			return;
		}
		removeTask(task.getID());
	}

	public synchronized static void removeTask(Long taskID) {
		TASKS.remove(taskID);
		LAST_ACTIVE_TIME_MAP.remove(taskID);
	}


	public synchronized static ReaperTask getTask() {
		return CURRENT_TASK.get();
	}


	/**
	 * 根据任务获取最后的活动时间
	 * 
	 * @param taskId
	 *            taskId
	 * @return Date Date
	 */
	public synchronized static Date getLastActiveTimeMap(Long taskId) {
		return LAST_ACTIVE_TIME_MAP.get(taskId);
	}

	/**
	 * 根据任务设置最后的活动时间
	 * 
	 * @param taskId
	 *            taskId
	 * @param currentDate
	 *            currentDate
	 * @return Date
	 */
	public synchronized static Date setLastActiveTimeMap(Long taskId, Date currentDate) {
		return LAST_ACTIVE_TIME_MAP.put(taskId, currentDate);
	}

	public synchronized static Map<Long, Date> getAllLastActiveTimeMap() {
		return LAST_ACTIVE_TIME_MAP;
	}

	public static void countError() {
		ERROR_COUNT++;
	}

	public synchronized static int getErrorCount() {
		return ERROR_COUNT;
	}
}
