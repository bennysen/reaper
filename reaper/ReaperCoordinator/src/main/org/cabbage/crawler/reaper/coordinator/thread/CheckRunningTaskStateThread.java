package org.cabbage.crawler.reaper.coordinator.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.json.Json;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.coordinator.conf.Configure;
import org.cabbage.crawler.reaper.coordinator.context.AppContext;
import org.cabbage.crawler.reaper.coordinator.das.bean.NSDomainBean;
import org.cabbage.crawler.reaper.coordinator.das.bean.NSProxyBean;
import org.cabbage.crawler.reaper.coordinator.das.bean.ReaperTaskBean;
import org.cabbage.crawler.reaper.coordinator.das.dao.NSDomainDAO;
import org.cabbage.crawler.reaper.coordinator.das.dao.NSProxyDAO;
import org.cabbage.crawler.reaper.coordinator.das.dao.ReaperTaskDAO;
import org.cabbage.crawler.reaper.coordinator.das.utils.QueryResult;
import org.cabbage.crawler.reaper.exception.ReaperException;

/**
 * update running task status for ReaperWorker
 * 
 * @author wkshen
 *
 */
public class CheckRunningTaskStateThread extends Thread {

	private static final Log LOGGER = LogFactory.getLog(CheckRunningTaskStateThread.class);

	private int taskMaxIdleTime = 600;

	/**
	 * 
	 * @param maxTaskCount
	 */
	public CheckRunningTaskStateThread() {
		Integer taskMaxIdleTime = null;
		try {
			taskMaxIdleTime = Configure.getInstance(false).getPropertyInteger("task_max_idle_time");
		} catch (ReaperException e) {
			LOGGER.warn("Get configure item[task_max_idle_time] error!", e);
		}
		if (null == taskMaxIdleTime || taskMaxIdleTime < 600) {

		} else {
			this.taskMaxIdleTime = taskMaxIdleTime;
		}
	}

	public void run() {

		while (true) {
			try {
				List<ReaperTask> tasks = scanTaskInProcessing();
				check(tasks);
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			} catch (Exception e1) {
				LOGGER.error(e1);
			}
		}
	}

	private void check(List<ReaperTask> tasks) {
		if (null == tasks || tasks.size() == 0) {
			return;
		}
		Long current = System.currentTimeMillis() / 1000;
		List<ReaperTaskBean> tasks4update = new ArrayList<ReaperTaskBean>();
		ReaperTaskDAO dao = (ReaperTaskDAO) AppContext.APP_CONTEXT.getBean("ReaperTaskDAO");
		for (ReaperTask task : tasks) {
			if (null == task) {
				continue;
			}
			if (null == task.getLastWorkTime()) {
				task.setLastWorkTime(0l);
			}
			if ((current - task.getLastWorkTime()) > taskMaxIdleTime) {
				ReaperTaskBean bean = AppContext.TASKID_2_TASK_BEAN.get(task.getID());
				if (null == bean) {
					Json j = new Json();
					j.a("ID", task.getID());
					QueryResult<ReaperTaskBean> r = dao.find(j, null, 0, 1);
					if (null == r || null == r.getResultlist() || r.getResultlist().size() == 0) {
						continue;
					}
					bean = r.getResultlist().get(0);
				}
				bean.setStatus(0l);
				bean.setProcessHost(null);
				tasks4update.add(bean);
			}
		}
		if (tasks4update.size() > 0) {
			dao.update(tasks4update, false);
		}
	}

	private List<ReaperTask> scanTaskInProcessing() {
		List<ReaperTask> tasks = new ArrayList<ReaperTask>();
		ReaperTaskDAO dao = (ReaperTaskDAO) AppContext.APP_CONTEXT.getBean("ReaperTaskDAO");
		Json query = new Json();
		query.a("status", new Long(2));
		Json order = new Json();
		order.a("lastUpdateTime", "desc");
		QueryResult<ReaperTaskBean> r = dao.find(query, order, 0, 64);
		if (null == r || null == r.getResultlist() || r.getResultlist().size() == 0) {
			LOGGER.info("No task in processing!");
		} else {
			NSDomainDAO dao2 = (NSDomainDAO) AppContext.APP_CONTEXT.getBean("NSDomainDAO");
			NSProxyDAO dao3 = (NSProxyDAO) AppContext.APP_CONTEXT.getBean("NSProxyDAO");
			for (ReaperTaskBean bean : r.getResultlist()) {
				Json q2 = new Json();
				q2.a("ID", bean.getDomainID());
				QueryResult<NSDomainBean> r2 = dao2.find(q2, null, 0, 1);
				if (null == r2 || null == r2.getResultlist() || r2.getResultlist().size() == 0) {
					LOGGER.warn("Can not find NSDomain[" + bean.getDomainID() + "]");
					continue;
				}
				NSDomainBean domain = r2.getResultlist().get(0);
				ReaperTask task = new ReaperTask();
				task.setID(bean.getID());
				task.setCharset(bean.getCharset());
				task.setCountryID(domain.getSITEAREACODE());
				task.setDomainID(bean.getDomainID());
				task.setDomainType(bean.getDomainType());
				task.setProxyType(task.getProxyType());
				if (null == task.getProxyType()) {

				} else {
					Json q3 = new Json();
					q3.a("proxyType", bean.getProxyType());
					QueryResult<NSProxyBean> r3 = dao3.find(q3, null, 0, 8);
					if (null == r2 || null == r2.getResultlist() || r2.getResultlist().size() == 0) {
						LOGGER.warn("Can not find NSProxy for ReaperTask[" + task.getID() + "],proxyType["
								+ bean.getProxyType() + "]");
					} else {
						NSProxyBean proxy = r3.getResultlist()
								.get(new java.util.Random().nextInt(r3.getResultlist().size()));
						task.setProxyIP(proxy.getIP());
						task.setProxyPort(proxy.getPort());
					}
				}
				task.setSiteName(domain.getDomainname());
				task.setSiteType(bean.getSiteType());
				task.setURL(domain.getDomainurl());
				tasks.add(task);
			}
		}
		return tasks;
	}

}
