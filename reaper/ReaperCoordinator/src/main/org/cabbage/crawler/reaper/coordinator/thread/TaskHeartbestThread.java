package org.cabbage.crawler.reaper.coordinator.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.json.Json;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.coordinator.context.AppContext;
import org.cabbage.crawler.reaper.coordinator.das.bean.ReaperTaskBean;
import org.cabbage.crawler.reaper.coordinator.das.dao.ReaperTaskDAO;
import org.cabbage.crawler.reaper.coordinator.das.utils.QueryResult;
import org.cabbage.crawler.reaper.coordinator.utils.RabbitMQUtils;

/**
 * update running task status for ReaperWorker
 * 
 * @author wkshen
 *
 */
public class TaskHeartbestThread extends Thread {

	private static final Log LOGGER = LogFactory.getLog(TaskHeartbestThread.class);

	/**
	 * 
	 * @param maxTaskCount
	 */
	public TaskHeartbestThread() {
	}

	public void run() {

		while (true) {
			try {
				List<ReaperTask> tasks = RabbitMQUtils.checkTaskHeartbeat(32);
				update(tasks);
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

	private void update(List<ReaperTask> tasks) {
		if (null == tasks || tasks.size() == 0) {
			return;
		}
		List<ReaperTaskBean> tasks4update = new ArrayList<ReaperTaskBean>();
		ReaperTaskDAO dao = (ReaperTaskDAO) AppContext.APP_CONTEXT.getBean("ReaperTaskDAO");
		for (ReaperTask task : tasks) {
			if (null == task || null == task.getID()) {
				continue;
			}
			if (null == task.getLastWorkTime()) {
				task.setLastWorkTime(System.currentTimeMillis() / 1000);
			}
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
			bean.setLastUpdateTime(task.getLastWorkTime());
			bean.setStatus(2l);
			tasks4update.add(bean);
		}
		if (tasks4update.size() > 0) {
			dao.update(tasks4update, false);
		}
	}

}
