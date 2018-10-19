package org.cabbage.crawler.reaper.worker.handle;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.file.FileUtils;
import org.cabbage.commons.utils.json.Json;
import org.cabbage.commons.web.servlet.base.BaseResult;
import org.cabbage.crawler.reaper.beans.business.communication.ResponseObject;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.exception.ReaperException;
import org.cabbage.crawler.reaper.worker.config.Configure;
import org.cabbage.crawler.reaper.worker.context.AppContext;
import org.cabbage.crawler.reaper.worker.exception.ReaperWorkerException;
import org.cabbage.crawler.reaper.worker.main.ReaperWorker;
import org.cabbage.crawler.reaper.worker.thread.RequestTaskThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ManagerHandle {

	private static final Log LOGGER = LogFactory.getLog(ManagerHandle.class);

	protected final Map<Long, WorkerHandle> runTaskList = new ConcurrentHashMap<Long, WorkerHandle>();

	protected final Map<Long, WorkerHandle> stopTaskList = new ConcurrentHashMap<Long, WorkerHandle>();

	public void checkAllTaskState() throws ReaperWorkerException {
		Iterator<Long> it = runTaskList.keySet().iterator();
		while (it.hasNext()) {
			try {
				Long id = (Long) it.next();
				ReaperTask task = (ReaperTask) runTaskList.get(id).getTask();
				WorkerHandle handle = runTaskList.get(id);

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
					runTaskList.remove(id);
					LOGGER.info("freeTaskFromDB(" + task.getID() + "," + handle + ")");
					freeTaskFromDB(task.getID(), handle);
					continue;
				} else {
					// 利用RMI接口实现心跳
					checkTaskHeartBeatRMI(task,
							Configure.getInstance(false).getPropertyInteger("maxTaskHeartbeatIdleSecond"), true,
							AppContext.getLastActiveTimeMap(task.getID()));
				}
			} catch (Exception e) {
				LOGGER.error("!!!!!!!!!!!!!!!!!!!!!! checkAllTaskState error!", e);
			}
		}
	}

	private void checkTaskHeartBeatRMI(ReaperTask task, int maxIdleTime, boolean isStop, Date lastActiveTime) {
		if (lastActiveTime != null) {
			int heartBeat = (int) (lastActiveTime.getTime() / 1000);
			if ((System.currentTimeMillis() / 1000 - heartBeat) > maxIdleTime) {
				LOGGER.warn("task " + task.getURL() + " timeout! ");
				if (isStop) {
					this.stopTask(task);
				}
				// TODO
				// CheckTaskStateThread.errorCount();
				LOGGER.warn("stop task id 4");
			}
		} else {
			// 已经没有了，被删除了，也要停止
			LOGGER.warn("task " + task.getURL() + " not exist int heart beat active map! force to stop!");
			if (isStop) {
				this.stopTask(task);
			}
			// TODO
			// CheckTaskStateThread.errorCount();
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
		if (runTaskList.containsKey(task.getID())) {
			LOGGER.info("Function【stopCrawlerTask】The runTaskList contains task[" + task.getID() + "," + task.getURL()
					+ "]!");
			WorkerHandle workHandle = runTaskList.get(task.getID());
			workHandle.stopTask("user force to end task");
			runTaskList.remove(task.getID());
			freeTaskFromDB(task.getID(), workHandle);
			return true;
		} else {
			LOGGER.info("Function【stopCrawlerTask】The runTaskList does not contains task[" + task.getID() + ","
					+ task.getURL() + "]!");
			freeTaskFromDB(task.getID(), null);
		}
		return false;
	}

	private void freeTaskFromDB(Long id, WorkerHandle handle) {
		stopTaskList.put(id, handle);
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
		if (runTaskList.containsKey(task.getID())) {
			LOGGER.warn("Task [" + task.getURL() + "] has been run.");
			return false;
		} else {
			AppContext.setLastActiveTimeMap(task.getID(), new Date());
			WorkerHandle workHandle = new WorkerHandle();
			workHandle.setTask(task);
			workHandle.initialize();
			runTaskList.put(task.getID(), workHandle);
			workHandle.runTask();
			return true;
		}
	}

	public Map<Long, WorkerHandle> getTaskList() {
		return runTaskList;
	}

	public List<ReaperTask> getTask(int number) throws ReaperException {
		LOGGER.info("getTask begin...");
		List<ReaperTask> tasks = null;
		String mqHost = Configure.getInstance(false).getProperty("mq_host");
		Integer mqPort = Configure.getInstance(false).getPropertyInteger("mq_port");
		String mqUsername = Configure.getInstance(false).getProperty("mq_username");
		String mqPassword = Configure.getInstance(false).getProperty("mq_password");
		String mqWaittingQueueName = Configure.getInstance(false).getProperty("mq_q_waitting");

		if (null == mqHost || mqHost.trim().length() == 0 || null == mqPort || mqPort < 0 || null == mqUsername
				|| mqUsername.trim().length() == 0 || null == mqPassword || mqPassword.trim().length() == 0
				|| null == mqWaittingQueueName || mqWaittingQueueName.trim().length() == 0) {
			LOGGER.error("Check configure file,MQ configures are invalid!");
			return null;
		} else {
			LOGGER.info("MQ configures for get ReaperTask[" + mqHost + ":" + mqPort + "/" + mqWaittingQueueName + "{"
					+ mqUsername + "/" + mqPassword + "}]");
		}
		String hostName = ReaperWorker.getDevice();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.80.101");
		factory.setPort(5672);
		factory.setUsername("admin");
		factory.setPassword("123456");

		Connection connection = null;
		Channel channel = null;

		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		LOGGER.info("getTask end!");
		return tasks;
	}

	@SuppressWarnings("unchecked")
	private List<ReaperTask> paser(String response) {
		List<ReaperTask> tasks = null;
		if (null == response || response.trim().length() == 0) {
			return null;
		}
		response = response.trim();
		Gson gsonBuilder = new GsonBuilder().create();
		BaseResult result = null;
		String msg = null;
		try {
			result = gsonBuilder.fromJson(response, BaseResult.class);
		} catch (Exception e) {
			LOGGER.error("Request task error!", e);
		}
		if (null != result) {
			if (result.isSuccess()) {
				msg = result.getMessage();
				if (null != msg && msg.trim().length() > 0) {
					Gson gson = new Gson();
					tasks = gson.fromJson(msg, new TypeToken<List<ReaperTask>>() {
					}.getType());
				}
			} else {
				LOGGER.error("Request task error!" + result.getMessage());
			}
		}
		return tasks;
	}

	public void responseTask() throws ReaperException {
		if (null == stopTaskList || stopTaskList.size() == 0) {
			return;
		} else {
			LOGGER.info("stopTaskList.size[" + stopTaskList.size() + "]");

		}
		List<ReaperTask> tasks = null;
		String mqHost = Configure.getInstance(false).getProperty("mq_host");
		Integer mqPort = Configure.getInstance(false).getPropertyInteger("mq_port");
		String mqUsername = Configure.getInstance(false).getProperty("mq_username");
		String mqPassword = Configure.getInstance(false).getProperty("mq_password");
		String mqWaittingQueueName = Configure.getInstance(false).getProperty("mq_q_finished");
		
		String hostName = ReaperWorker.getDevice();
		
		Iterator<Long> it = stopTaskList.keySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			if (count > 30) {
				break;
			}
			Long id = it.next();
			if (null == id||id == 0) {
				continue;
			}
			count++;
			ResponseObject ro = AppContext.getResponseObject(id);
			if (null == ro) {
				ro = new ResponseObject();
				//TODO
			}
			WorkerHandle handle = stopTaskList.get(id);

			if (null != handle) {
				ReaperTask task = handle.getTask();
				if (null != task) {
					//TODO
				} else {
					LOGGER.error("task[" + id + "] handle.getTask() is null");
				}
			}
		}
		LOGGER.info("Response tasks is finished![" + count + "]");
	}

}
