package org.cabbage.crawler.reaper.worker.handle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.exception.ReaperException;
import org.cabbage.crawler.reaper.worker.config.Configure;
import org.cabbage.crawler.reaper.worker.context.AppContext;
import org.cabbage.crawler.reaper.worker.exception.ReaperWorkerException;
import org.cabbage.crawler.reaper.worker.utils.RabbitMQUtils;
import org.omg.CORBA.portable.ApplicationException;

public class ManagerHandle {

	private static ManagerHandle INSTANCE = null;

	private ManagerHandle() {
	}

	public synchronized static ManagerHandle getInstance() {
		if(null==INSTANCE) {
			INSTANCE = new ManagerHandle();
		}
		return INSTANCE;
	}

	private static final Log LOGGER = LogFactory.getLog(ManagerHandle.class);

	protected final Map<Long, WorkerHandle> runTaskMap = new ConcurrentHashMap<Long, WorkerHandle>();

	protected final Map<Long, WorkerHandle> stopTaskMap = new ConcurrentHashMap<Long, WorkerHandle>();

	public void checkAllTaskState() throws ReaperWorkerException {
		Iterator<Long> it = runTaskMap.keySet().iterator();
		while (it.hasNext()) {
			try {
				Long id = (Long) it.next();
				ReaperTask task = (ReaperTask) runTaskMap.get(id).getTask();
				WorkerHandle handle = runTaskMap.get(id);

				if (null == handle) {
					LOGGER.warn("AbstractApplicationHandle is null!");
					continue;
				}
				// 如果任务状态是停止、错误或者不再运行，则从任务表中清除，不用心跳
				if (handle.getWorkerState() == ReaperTask.FINISH || handle.getWorkerState() == ReaperTask.STOP
						|| handle.getWorkerState() == ReaperTask.DISABLE) {
					LOGGER.info("task " + task.getURL() + " finished.");
					it.remove();
					task.setLastWorkTime(System.currentTimeMillis() / 1000);
					AppContext.getAllLastActiveTimeMap().remove(task.getID());
					runTaskMap.remove(id);
					stopTaskMap.put(id, handle);
					continue;
				} else {
					// 利用RMI接口实现心跳
					checkTaskHeartBeat(task,
							Configure.getInstance(false).getPropertyInteger("maxTaskHeartbeatIdleSecond"), true,
							AppContext.getLastActiveTimeMap(task.getID()));
				}
			} catch (Exception e) {
				LOGGER.error("!!!!!!!!!!!!!!!!!!!!!! checkAllTaskState error!", e);
			}
		}
	}

	private void checkTaskHeartBeat(ReaperTask task, int maxIdleTime, boolean isStop, Date lastActiveTime) {
		if (lastActiveTime != null) {
			int heartBeat = (int) (lastActiveTime.getTime() / 1000);
			if ((System.currentTimeMillis() / 1000 - heartBeat) > maxIdleTime) {
				LOGGER.warn("task [" + task.getURL() + "] timeout! ");
				if (isStop) {
					this.stopTask(task);
				}
				AppContext.countError();
				LOGGER.warn("stop task id 4");
			}
		} else {
			// 已经没有了，被删除了，也要停止
			LOGGER.warn("task [" + task.getURL() + "] not exist in heart beat active map! force to stop!");
			if (isStop) {
				this.stopTask(task);
			}
			AppContext.countError();
			LOGGER.warn("stop task id 3");
		}
	}

	/**
	 * 停止一个正在运行的采集任务，并把它从任务列表中移除
	 * 
	 * @param task
	 *            要停止的采集任务
	 * @throws Exception
	 *             停止采集任务出错或者任务不存在
	 */
	public boolean stopTask(ReaperTask task) {
		if (null == task || null == task.getID()) {
			return false;
		}
		if (runTaskMap.containsKey(task.getID())) {
			LOGGER.info("Function【stopTask】The runTaskList contains task[" + task.getID() + "," + task.getURL() + "]!");
			WorkerHandle workHandle = runTaskMap.get(task.getID());
			workHandle.stopTask();
			runTaskMap.remove(task.getID());
			stopTaskMap.put(task.getID(), workHandle);
			AppContext.removeTask(task.getID());
			return true;
		} else {
			LOGGER.info("Function【stopTask】The runTaskList does not contains task[" + task.getID() + "," + task.getURL()
					+ "]!");
			stopTaskMap.put(task.getID(), null);
		}
		return false;
	}

	/**
	 * 使用自己的workerhandle来运行一个任务句柄
	 * 
	 * @param task
	 *            运行的新闻任务
	 * @return true：运行成功；false：运行失败
	 * @throws ApplicationException
	 *             运行出现的异常
	 */
	public boolean runTask(ReaperTask task) throws ReaperWorkerException {
		if (runTaskMap.containsKey(task.getID())) {
			LOGGER.warn("Task [" + task.getURL() + "] has been run.");
			return false;
		} else {
			AppContext.setLastActiveTimeMap(task.getID(), new Date());
			WorkerHandle workHandle = new WorkerHandle(task);
			runTaskMap.put(task.getID(), workHandle);
			workHandle.runTask();
			return true;
		}
	}

	public Map<Long, WorkerHandle> getRunTasks() {
		return runTaskMap;
	}
	
	public Map<Long, WorkerHandle> getStopTasks() {
		return stopTaskMap;
	}

	public List<ReaperTask> getTask(int number) throws ReaperException, IOException, TimeoutException {
		LOGGER.info("getTask begin...");
		return RabbitMQUtils.getWaittingTask(number);
	}

	public void responseTask() throws ReaperException {
		if (null == stopTaskMap || stopTaskMap.size() == 0) {
			return;
		} else {
			LOGGER.info("stopTaskList.size[" + stopTaskMap.size() + "]");

		}
		List<ReaperTask> tasks = new ArrayList<ReaperTask>();

		Iterator<Long> it = stopTaskMap.keySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			if (count > 30) {
				break;
			}
			Long id = it.next();
			if (null == id || id == 0) {
				continue;
			}
			count++;
			WorkerHandle handle = stopTaskMap.get(id);
			if (null != handle) {
				ReaperTask task = handle.getTask();
				if (null != task) {
					tasks.add(task);
				} else {
					LOGGER.error("task[" + id + "] handle.getTask() is null");
				}
			}
		}
		String mqFinishedQueueName = Configure.getInstance(false).getProperty("mq_q_finished");
		if (null == mqFinishedQueueName || mqFinishedQueueName.trim().length() == 0) {
			LOGGER.error("Check configure file,mq_q_finished queue is invalid!");
		} else {
			try {
				RabbitMQUtils.sendTask(mqFinishedQueueName, tasks);
				for (ReaperTask t : tasks) {
					stopTaskMap.remove(t.getID());
				}
			} catch (IOException e) {
				LOGGER.error("Response tasks error!", e);
			} catch (TimeoutException e) {
				LOGGER.error("Response tasks error!", e);
			}
		}
		LOGGER.info("Response tasks is finished![" + count + "]");
	}

}
