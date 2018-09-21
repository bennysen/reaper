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
import org.cabbage.crawler.reaper.commons.object.business.task.ReaperTask;
import org.cabbage.crawler.reaper.exception.ReaperException;
import org.cabbage.crawler.reaper.worker.config.Configure;
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

	protected final Map<String, WorkerHandle> runTaskList = new ConcurrentHashMap<String, WorkerHandle>();

	protected final Map<String, WorkerHandle> stopTaskList = new ConcurrentHashMap<String, WorkerHandle>();

	public void checkAllTaskState() throws ReaperWorkerException {
		Iterator<String> it = runTaskList.keySet().iterator();
		for (; it.hasNext();) {
			try {
				String id = (String) it.next();
				ReaperTask task = (ReaperTask) runTaskList.get(id).getTask();
				AbstractApplicationHandle handle = runTaskList.get(id);

				if (null == handle) {
					LOGGER.warn("AbstractApplicationHandle is null!");
					continue;
				}
				// 如果任务状态是停止、错误或者不再运行，则从任务表中清除，不用心跳
				if (handle.getWorkerState() == com.fh.netpf.common.taskmgr.TaskBase.FINISH
						|| handle.getWorkerState() == com.fh.netpf.common.taskmgr.TaskBase.STOP
						|| handle.getWorkerState() == com.fh.netpf.common.taskmgr.TaskBase.DISABLE) {
					LOGGER.info("task " + task.getName() + " finished.");
					it.remove();
					task.setLastWorkTime(new Date());
					AppContext.getAllLastActiveTimeMap().remove(task.getId());
					runTaskList.remove(id);
					LOGGER.info("freeTaskFromDB(" + task.getId() + "," + handle + ")");
					freeTaskFromDB(task.getId() + "", handle);
					continue;
				} else {
					// 如果没有完成，则查看其心跳是否超时
					CrawlerConfigure conf = (CrawlerConfigure) configure;
					// 利用RMI接口实现心跳
					checkTaskHeartBeatRMI(task, conf.getMaxIdleTime(), true,
							AppContext.getLastActiveTimeMap(String.valueOf(task.getId())));
				}
			} catch (Exception e) {
				LOGGER.error("!!!!!!!!!!!!!!!!!!!!!! checkAllTaskState error!", e);
			}
		}
	}

	public boolean runTask(TaskBase task) throws ReaperWorkerException {
		if (task instanceof ReaperTask) {
			ReaperTask t = (ReaperTask) task;
			String domain = "";
			if (null != t.getUrl()) {
				domain = NetUtil.getDomain(t.getUrl().trim());
			}
			return runNewsCrawlerTask(t);
		} else {
			LOGGER.fatal("Task is not ReaperTask!");
			return false;
		}
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
	protected boolean runNewsCrawlerTask(ReaperTask task) throws ReaperWorkerException {
		if (runTaskList.containsKey(task.getId() + "")) {
			LOGGER.warn("Task [" + task.getName() + "] has been run.");
			return false;
		} else {
			AppContext.setLastActiveTimeMap(task.getId() + "", new Date());
			WorkerHandle workHandle = new WorkerHandle();
			workHandle.setTask(task);
			workHandle.setModule(module);
			workHandle.initialize();
			runTaskList.put(task.getId() + "", workHandle);
			workHandle.runTask();
			return true;
		}
	}

	public Map<String, AbstractApplicationHandle> getTaskList() {
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

	public void responseTask() {
		if (null == stopTaskList || stopTaskList.size() == 0) {
			return;
		} else {
			LOGGER.info("stopTaskList.size[" + stopTaskList.size() + "]");

		}
		String response = ((CrawlerConfigure) this.configure).getResponseTaskAddress();
		if (null == response || response.trim().length() == 0) {
			LOGGER.error("Check netpf-crawler.xml ,responseTaskAddress is null!");
			return;
		}
		String hostName = ((CrawlerConfigure) this.configure).getDevice();
		response = response.trim();
		Iterator<String> it = stopTaskList.keySet().iterator();
		Set<String> set = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		List<ResponseObject> lr = new ArrayList<ResponseObject>();
		int count = 0;
		while (it.hasNext()) {
			if (count > 30) {
				break;
			}
			String id = (String) it.next();
			count++;
			if (null == id) {
				continue;
			}
			if (id.trim().length() == 0) {
				stopTaskList.remove(id);
				continue;
			}
			ResponseObject ro = AppContext.getResponseObject(Long.parseLong(id));
			if (null == ro) {
				ro = new ResponseObject();
				ro.setID(Long.parseLong(id));
				ro.setExecutionResult(1l);
			}
			AbstractApplicationHandle handle = stopTaskList.get(id);
			String url1 = null;
			String url2 = null;

			if (null != handle) {
				TaskBase task = handle.getTask();
				if (null != task) {
					ReaperTask t1 = (ReaperTask) task;
					if (null != t1) {
						url1 = t1.getUrl();
						if (null != url1) {
							set2.add(url1);
						}
						ReaperTask t2 = AppContext.getTask(t1.getUrl());
						if (null != t2 && null != t2.getServerExclude() && t2.getServerExclude().trim().length() > 0) {
							ro.setServerExclude(t2.getServerExclude().trim());
							url2 = t2.getUrl();
							if (null != url2) {
								set2.add(url2);
							}
						}
					}
				} else {
					LOGGER.error("task[" + id + "] handle.getTask() is null");
				}
			}
			lr.add(ro);
			set.add(id);
		}
		LOGGER.info("scan stopTaskList is finished![" + lr.size() + "] " + "stopTaskList.size[" + stopTaskList.size()
				+ "] " + "set.size[" + set.size() + "] " + "set2.size[" + set2.size() + "] ");
		if (lr.size() > 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			List<Json> lj = new ArrayList<Json>();
			for (ResponseObject ro : lr) {
				Json j = new Json(ro);
				lj.add(j);
			}
			Json json = new Json();
			json.al("responseList", lj);
			params.put("msg", json.toString());
			params.put("hostName", hostName);
			try {
				LOGGER.info("Response Api begin:" + json.toString());
				response = HttpQuery1.post(response, params);
				LOGGER.info("Response:" + response);
				LOGGER.info("Response Api successed!");

				for (String id : set) {
					stopTaskList.remove(id);
					LOGGER.info("stopTaskList.remove(" + id + ")");
					AppContext.removeTaskCheckTime(id);
				}
				for (String url : set2) {
					ReaperTask task = new ReaperTask();
					task.setUrl(url);
					AppContext.removeTask(task);
				}
			} catch (SocketException e1) {
				LOGGER.error("", e1);
				if (null != e1.getMessage() && (e1.getMessage().trim().equals("Too many open files")
						|| e1.getMessage().trim().equals("打开的文件过多"))) {
					URLDetectorWorkerManager.tooManyOpenFileCount();
				}
			} catch (IOException e) {
				LOGGER.error(e);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		LOGGER.info("Response tasks is finished![" + lr.size() + "]");
	}

}
