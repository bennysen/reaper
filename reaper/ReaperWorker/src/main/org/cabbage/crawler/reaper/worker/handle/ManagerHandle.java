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

import org.cabbage.commons.utils.file.FileUtils;
import org.cabbage.commons.utils.json.Json;
import org.cabbage.commons.web.servlet.base.BaseResult;
import org.cabbage.crawler.reaper.worker.exception.ReaperWorkerException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ManagerHandle {

	protected final Map<String, WorkerHandle> runTaskList = new ConcurrentHashMap<String, WorkerHandle>();

	protected final Map<String, WorkerHandle> stopTaskList = new ConcurrentHashMap<String, WorkerHandle>();

	public void checkAllTaskState() throws ReaperWorkerException {
		Iterator<String> it = runTaskList.keySet().iterator();
		for (; it.hasNext();) {
			try {
				String id = (String) it.next();
				SpiderTask task = (SpiderTask) runTaskList.get(id).getTask();
				AbstractApplicationHandle handle = runTaskList.get(id);

				if (null == handle) {
					logger.warn("AbstractApplicationHandle is null!");
					continue;
				}
				// 如果任务状态是停止、错误或者不再运行，则从任务表中清除，不用心跳
				if (handle.getWorkerState() == com.fh.netpf.common.taskmgr.TaskBase.FINISH
						|| handle.getWorkerState() == com.fh.netpf.common.taskmgr.TaskBase.STOP
						|| handle.getWorkerState() == com.fh.netpf.common.taskmgr.TaskBase.DISABLE) {
					logger.info("task " + task.getName() + " finished.");
					it.remove();
					task.setLastWorkTime(new Date());
					AppContext.getAllLastActiveTimeMap().remove(task.getId());
					runTaskList.remove(id);
					logger.info("freeTaskFromDB(" + task.getId() + "," + handle
							+ ")");
					freeTaskFromDB(task.getId() + "", handle);
					continue;
				} else {
					// 如果没有完成，则查看其心跳是否超时
					CrawlerConfigure conf = (CrawlerConfigure) configure;
					// 利用RMI接口实现心跳
					checkTaskHeartBeatRMI(task, conf.getMaxIdleTime(), true,
							AppContext.getLastActiveTimeMap(String.valueOf(task
									.getId())));
				}
			} catch (Exception e) {
				logger.error("!!!!!!!!!!!!!!!!!!!!!! checkAllTaskState error!",
						e);
			}
		}
	}

	public boolean runTask(TaskBase task) throws ReaperWorkerException {
		if (task instanceof SpiderTask) {
			SpiderTask t = (SpiderTask) task;
			String domain = "";
			if (null != t.getUrl()) {
				domain = NetUtil.getDomain(t.getUrl().trim());
			}
			return runNewsCrawlerTask(t);
		} else {
			logger.fatal("Task is not SpiderTask!");
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
	protected boolean runNewsCrawlerTask(SpiderTask task)
			throws ReaperWorkerException {
		if (runTaskList.containsKey(task.getId() + "")) {
			logger.warn("Task [" + task.getName() + "] has been run.");
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

	public List<SpiderTask> requestTask(int number) {
		logger.info("   RequestTask begin...");
		List<SpiderTask> tasks = null;
		String request = ((CrawlerConfigure) this.configure)
				.getRequestTaskAddress();
		if (null == request || request.trim().length() == 0) {
			logger
					.error("Check netpf-crawler.xml ,requestTaskAddress is null!");
			return null;
		} else {
			logger.info(request);
		}
		request = request.trim();
		String hostName = ((CrawlerConfigure) this.configure).getDevice();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hostName", hostName);
		params.put("num", number + "");
		params.put("module", module);
		String response = null;
		try {
			response = HttpQuery.post(request, params);
			tasks = paser(response);
		} catch (SocketException e1) {
			logger.error("", e1);
			if (null != e1.getMessage()
					&& (e1.getMessage().trim().equals("Too many open files") || e1
							.getMessage().trim().equals("打开的文件过多"))) {
				URLDetectorWorkerManager.tooManyOpenFileCount();
			}
		} catch (IOException e) {
			logger.error(e);
		}
		logger.info("   RequestTask end!!!!!");
		return tasks;
	}

	@SuppressWarnings("unchecked")
	private List<SpiderTask> paser(String response) {
		List<SpiderTask> tasks = null;
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
			logger.error("Request task error!", e);
		}
		if (null != result) {
			if (result.isSuccess()) {
				msg = result.getMessage();
				if (null != msg && msg.trim().length() > 0) {
					Gson gson = new Gson();
					tasks = gson.fromJson(msg,
							new TypeToken<List<SpiderTask>>() {
							}.getType());
				}
			} else {
				logger.error("Request task error!" + result.getMessage());
			}
		}
		return tasks;
	}

	public void responseTask() {
		if (null == stopTaskList || stopTaskList.size() == 0) {
			return;
		} else {
			logger.info("stopTaskList.size[" + stopTaskList.size() + "]");

		}
		String response = ((CrawlerConfigure) this.configure)
				.getResponseTaskAddress();
		if (null == response || response.trim().length() == 0) {
			logger
					.error("Check netpf-crawler.xml ,responseTaskAddress is null!");
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
			ResponseObject ro = AppContext
					.getResponseObject(Long.parseLong(id));
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
					SpiderTask t1 = (SpiderTask) task;
					if (null != t1) {
						url1 = t1.getUrl();
						if (null != url1) {
							set2.add(url1);
						}
						SpiderTask t2 = AppContext.getTask(t1.getUrl());
						if (null != t2 && null != t2.getServerExclude()
								&& t2.getServerExclude().trim().length() > 0) {
							ro.setServerExclude(t2.getServerExclude().trim());
							url2 = t2.getUrl();
							if (null != url2) {
								set2.add(url2);
							}
						}
					}
				} else {
					logger.error("task[" + id + "] handle.getTask() is null");
				}
			}
			lr.add(ro);
			set.add(id);
		}
		logger.info("scan stopTaskList is finished![" + lr.size() + "] "
				+ "stopTaskList.size[" + stopTaskList.size() + "] "
				+ "set.size[" + set.size() + "] " + "set2.size[" + set2.size()
				+ "] ");
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
				logger.info("Response Api begin:" + json.toString());
				response = HttpQuery1.post(response, params);
				logger.info("Response:" + response);
				logger.info("Response Api successed!");

				for (String id : set) {
					stopTaskList.remove(id);
					logger.info("stopTaskList.remove(" + id + ")");
					AppContext.removeTaskCheckTime(id);
				}
				for (String url : set2) {
					SpiderTask task = new SpiderTask();
					task.setUrl(url);
					AppContext.removeTask(task);
				}
			} catch (SocketException e1) {
				logger.error("", e1);
				if (null != e1.getMessage()
						&& (e1.getMessage().trim()
								.equals("Too many open files") || e1
								.getMessage().trim().equals("打开的文件过多"))) {
					URLDetectorWorkerManager.tooManyOpenFileCount();
				}
			} catch (IOException e) {
				logger.error(e);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.info("Response tasks is finished![" + lr.size() + "]");
	}


}
