package org.cabbage.crawler.reaper.worker.context;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.beans.business.communication.ResponseObject;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;

public class AppContext {

	private static final Log LOGGER = LogFactory.getLog(AppContext.class);

	private static ThreadLocal<ReaperTask> CURRENT_TASK = new ThreadLocal<ReaperTask>();

	/**
	 * Manager中处理的任务Map<Long,Date>
	 */
	private static Map<Long, Date> LAST_ACTIVE_TIME_MAP = new ConcurrentHashMap<Long, Date>();

	private static Map<Long, ReaperTask> TASKS = new ConcurrentHashMap<Long, ReaperTask>();

	private static Map<Long, ResponseObject> TASKID_2_RESPONSE = new ConcurrentHashMap<Long, ResponseObject>();

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
		removeTaskCheckTime(taskID);
	}

	public synchronized static void removeTaskCheckTime(Long taskid) {
		if (null != taskid) {
			LAST_ACTIVE_TIME_MAP.remove(taskid);
			TASKID_2_RESPONSE.remove(taskid);
		}
	}

	public synchronized static ReaperTask getTask() {
		return CURRENT_TASK.get();
	}

	public synchronized static void removeTaskResponseObject(Long taskid) {
		if (null != taskid) {
			TASKID_2_RESPONSE.remove(taskid);
		}
	}

	public synchronized static ResponseObject getResponseObject(Long taskid) {
		return TASKID_2_RESPONSE.get(taskid);
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
	public synchronized static Date setLastActiveTimeMap(Long taskId,
			Date currentDate) {
		return LAST_ACTIVE_TIME_MAP.put(taskId, currentDate);
	}


	public synchronized static Map<Long, Date> getAllLastActiveTimeMap() {
		return LAST_ACTIVE_TIME_MAP;
	}
}
