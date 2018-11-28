package org.cabbage.crawler.reaper.coordinator.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.json.Json;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.coordinator.conf.Configure;
import org.cabbage.crawler.reaper.coordinator.context.AppContext;
import org.cabbage.crawler.reaper.coordinator.das.bean.NSDomainBean;
import org.cabbage.crawler.reaper.coordinator.das.bean.NSProxyBean;
import org.cabbage.crawler.reaper.coordinator.das.bean.NSScriptBean;
import org.cabbage.crawler.reaper.coordinator.das.bean.ReaperTaskBean;
import org.cabbage.crawler.reaper.coordinator.das.dao.NSDomainDAO;
import org.cabbage.crawler.reaper.coordinator.das.dao.NSProxyDAO;
import org.cabbage.crawler.reaper.coordinator.das.dao.NSScriptDAO;
import org.cabbage.crawler.reaper.coordinator.das.dao.ReaperTaskDAO;
import org.cabbage.crawler.reaper.coordinator.das.utils.QueryResult;
import org.cabbage.crawler.reaper.coordinator.utils.RabbitMQUtils;
import org.cabbage.crawler.reaper.exception.ReaperException;

/**
 * Produce task into mq(mq_q_waitting) for ReaperWorker
 * 
 * @author wkshen
 *
 */
public class ProduceTaskThread extends Thread {

	private static final Log LOGGER = LogFactory.getLog(ProduceTaskThread.class);

	/**
	 * 
	 * @param maxTaskCount
	 */
	public ProduceTaskThread() {
	}

	public void run() {

		while (true) {
			try {
				List<ReaperTask> tasks = scanTask4Produce();
				sendTask2MQ(tasks);
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			} catch (Exception e1) {
				LOGGER.error("Produce task error!", e1);
			}
		}
	}

	private void sendTask2MQ(List<ReaperTask> tasks) throws ReaperException, IOException, TimeoutException {
		if (null == tasks || tasks.size() == 0) {
			return;
		}
		String queue = Configure.getInstance(false).getProperty("mq_q_waitting");
		RabbitMQUtils.sendTask(queue, tasks);
	}

	private List<ReaperTask> scanTask4Produce() {
		List<ReaperTask> tasks = new ArrayList<ReaperTask>();
		List<ReaperTaskBean> tasks4update = new ArrayList<ReaperTaskBean>();
		ReaperTaskDAO dao = (ReaperTaskDAO) AppContext.APP_CONTEXT.getBean("ReaperTaskDAO");
		Json query = new Json();
		query.a("status", new Long(0));
		query.a("enable", new Long(1));
		Json order = new Json();
		order.a("lastUpdateTime", "asc");
		QueryResult<ReaperTaskBean> r = dao.find(query, order, 0, 64);
		if (null == r || null == r.getResultlist() || r.getResultlist().size() == 0) {
			LOGGER.info("No task to produce!");
			return null;
		}
		NSDomainDAO dao2 = (NSDomainDAO) AppContext.APP_CONTEXT.getBean("NSDomainDAO");
		NSProxyDAO dao3 = (NSProxyDAO) AppContext.APP_CONTEXT.getBean("NSProxyDAO");
		NSScriptDAO dao4 = (NSScriptDAO) AppContext.APP_CONTEXT.getBean("NSScriptDAO");
		for (ReaperTaskBean bean : r.getResultlist()) {
			Json q2 = new Json();
			q2.a("ID", bean.getDomainID());
			QueryResult<NSDomainBean> r2 = dao2.find(q2, null, 0, 1);
			if (null == r2 || null == r2.getResultlist() || r2.getResultlist().size() == 0) {
				bean.setLastUpdateTime(System.currentTimeMillis() / 1000);
				tasks4update.add(bean);
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

			Json q4 = new Json();
			q4.a("domainID", domain.getID());
			q4.a("status", 1l);
			QueryResult<NSScriptBean> r4 = dao4.find(q4, null, 0, 8);
			if (null == r4 || null == r4.getResultlist() || r4.getResultlist().size() == 0) {
				bean.setLastUpdateTime(System.currentTimeMillis() / 1000);
				tasks4update.add(bean);
				LOGGER.warn("Can not find NSProxy[" + bean.getDomainID() + "]");
//				continue;
			}
			List<Long> scriptIDs = new ArrayList<Long>();
			for (NSScriptBean script : r4.getResultlist()) {
				scriptIDs.add(script.getID());
			}
			task.setScriptIDs(scriptIDs);
			tasks.add(task);
			bean.setStatus(1l);
			bean.setLastUpdateTime(System.currentTimeMillis() / 1000);
			tasks4update.add(bean);
			AppContext.TASKID_2_TASK_BEAN.put(bean.getID(), bean);
			AppContext.TASKID_2_TASK.put(task.getID(), task);
		}
		dao.update(tasks4update, false);

		return tasks;
	}

}
