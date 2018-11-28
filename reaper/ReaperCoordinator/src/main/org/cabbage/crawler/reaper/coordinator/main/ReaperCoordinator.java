package org.cabbage.crawler.reaper.coordinator.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.json.Json;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.coordinator.context.AppContext;
import org.cabbage.crawler.reaper.coordinator.das.bean.NSDomainBean;
import org.cabbage.crawler.reaper.coordinator.das.bean.NSProxyBean;
import org.cabbage.crawler.reaper.coordinator.das.bean.ReaperTaskBean;
import org.cabbage.crawler.reaper.coordinator.das.dao.NSDomainDAO;
import org.cabbage.crawler.reaper.coordinator.das.dao.NSProxyDAO;
import org.cabbage.crawler.reaper.coordinator.das.dao.ReaperTaskDAO;
import org.cabbage.crawler.reaper.coordinator.das.utils.QueryResult;
import org.cabbage.crawler.reaper.coordinator.thread.CheckFinishedTaskThread;
import org.cabbage.crawler.reaper.coordinator.thread.CheckProcessingTaskThread;
import org.cabbage.crawler.reaper.coordinator.thread.CheckResetTaskThread;
import org.cabbage.crawler.reaper.coordinator.thread.CheckRunningTaskStateThread;
import org.cabbage.crawler.reaper.coordinator.thread.ProduceTaskThread;
import org.cabbage.crawler.reaper.coordinator.thread.TaskHeartbestThread;

public class ReaperCoordinator {

	private static final Log LOGGER = LogFactory.getLog(ReaperCoordinator.class);

	private static void init() {
		ReaperTaskDAO dao = (ReaperTaskDAO) AppContext.APP_CONTEXT.getBean("ReaperTaskDAO");
		Json query = new Json();
		query.a("status", new Long(1));
		// query.a("enable", new Long(1));
		Json order = new Json();
		order.a("lastUpdateTime", "asc");
		QueryResult<ReaperTaskBean> r = dao.find(query, order, 0, 10000);
		if (null == r || null == r.getResultlist() || r.getResultlist().size() == 0) {
			LOGGER.info("No task in processing!");
		} else {
			NSDomainDAO dao2 = (NSDomainDAO) AppContext.APP_CONTEXT.getBean("NSDomainDAO");
			NSProxyDAO dao3 = (NSProxyDAO) AppContext.APP_CONTEXT.getBean("NSProxyDAO");
			for (ReaperTaskBean bean : r.getResultlist()) {
				AppContext.TASKID_2_TASK_BEAN.put(bean.getID(), bean);
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
				AppContext.TASKID_2_TASK.put(task.getID(), task);
			}
		}
	}

	private static void checkAllTaskState() {
		CheckRunningTaskStateThread thread = new CheckRunningTaskStateThread();
		thread.start();
	}

	private static void checkTaskHeartbeat() {
		TaskHeartbestThread thread = new TaskHeartbestThread();
		thread.start();
	}

	private static void checkReset() {
		CheckResetTaskThread thread = new CheckResetTaskThread();
		thread.start();
	}

	private static void checkFinished() {
		CheckFinishedTaskThread thread = new CheckFinishedTaskThread();
		thread.start();
	}
	

	private static void checkProcessing() {
		CheckProcessingTaskThread thread = new CheckProcessingTaskThread();
		thread.start();
	}

	private static void produce() {
		ProduceTaskThread thread = new ProduceTaskThread();
		thread.start();
	}

	public static void main(String[] args) {

		checkReset();

		init();

		checkAllTaskState();

		produce();

		checkFinished();

		checkTaskHeartbeat();

		checkProcessing();
	}


}
