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
 * check finished task in mq queue
 * 
 * @author wkshen
 *
 */
public class CheckFinishedTaskThread extends Thread {

	private static final Log LOGGER = LogFactory.getLog(CheckFinishedTaskThread.class);

	/**
	 * 
	 */
	public CheckFinishedTaskThread() {
	}

	public void run() {

		while (true) {
			try {

				List<ReaperTask> ts = RabbitMQUtils.getFinishedTask(32);

				if (null == ts || ts.size() == 0) {

				} else {
					ReaperTaskDAO dao = (ReaperTaskDAO) AppContext.APP_CONTEXT.getBean("ReaperTaskDAO");
					List<ReaperTaskBean> list4update = new ArrayList<ReaperTaskBean>();
					for (int i = 0; i < ts.size(); i++) {
						ReaperTask task = ts.get(i);
						if (null == task || task.isInvalid()) {
							LOGGER.warn("No task to update[" + i + "]");
						} else {
							LOGGER.info("Update task [" + task.getID() + "]");
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
							bean.setLastUpdateTime(System.currentTimeMillis() / 1000);
							bean.setStatus(0l);
							bean.setProcessHost(null);
							list4update.add(bean);

						}
					}
					if (null != list4update && list4update.size() > 0) {
						dao.update(list4update, false);
					}
				}
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
}
