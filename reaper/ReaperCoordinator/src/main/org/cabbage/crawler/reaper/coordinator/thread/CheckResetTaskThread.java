package org.cabbage.crawler.reaper.coordinator.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.json.Json;
import org.cabbage.crawler.reaper.coordinator.context.AppContext;
import org.cabbage.crawler.reaper.coordinator.das.bean.ReaperTaskBean;
import org.cabbage.crawler.reaper.coordinator.das.dao.ReaperTaskDAO;
import org.cabbage.crawler.reaper.coordinator.das.utils.QueryResult;
import org.cabbage.crawler.reaper.coordinator.utils.RabbitMQUtils;

/**
 * Reset task status for ReaperWorker
 * 
 * @author wkshen
 *
 */
public class CheckResetTaskThread extends Thread {

	private static final Log LOGGER = LogFactory.getLog(CheckResetTaskThread.class);

	/**
	 * 
	 * @param maxTaskCount
	 */
	public CheckResetTaskThread() {
	}

	public void run() {

		while (true) {
			try {
				List<String> hosts = RabbitMQUtils.getResetHost(32);
				reset(hosts);
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			} catch (Exception e1) {
				LOGGER.error(e1);
			}
		}
	}

	private void reset(List<String> hosts) {
		if (null == hosts || hosts.size() == 0) {
			return;
		}
		List<ReaperTaskBean> tasks4update = new ArrayList<ReaperTaskBean>();
		ReaperTaskDAO dao = (ReaperTaskDAO) AppContext.APP_CONTEXT.getBean("ReaperTaskDAO");
		for (int i = 0; i < hosts.size(); i++) {
			String host = hosts.get(i);
			if (null == host || host.trim().length() == 0) {
				LOGGER.warn("No host to reset[" + i + "]");
			} else {
				LOGGER.info("Reset host [" + host + "],");
				Json query = new Json();
				query.a("processHost", host);
				QueryResult<ReaperTaskBean> r = dao.find(query, null, 0, 1024);
				if (null == r || r.getTotalrecord() == 0 || null == r.getResultlist()
						|| r.getResultlist().size() == 0) {
					continue;
				}

				for (ReaperTaskBean task : r.getResultlist()) {
					task.setProcessHost(null);
					task.setStatus(0l);
					tasks4update.add(task);
				}
			}
		}
		dao.update(tasks4update, false);
	}

}
