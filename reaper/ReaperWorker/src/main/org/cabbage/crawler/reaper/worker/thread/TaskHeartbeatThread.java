package org.cabbage.crawler.reaper.worker.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.exception.ReaperException;
import org.cabbage.crawler.reaper.worker.config.Configure;
import org.cabbage.crawler.reaper.worker.context.AppContext;
import org.cabbage.crawler.reaper.worker.utils.RabbitMQUtils;

public class TaskHeartbeatThread extends Thread {

	private static final Log LOGGER = LogFactory.getLog(TaskHeartbeatThread.class);

	public TaskHeartbeatThread() {
		
	}

	public void run() {
		while (true) {
			try {
				Map<Long, Date> map = AppContext.getAllLastActiveTimeMap();
				if (null == map || map.size() == 0) {
					try {
						Thread.sleep(60000);
					} catch (Exception e) {
						LOGGER.error(e);
					}
				} else {
					process(map);
				}
				try {
					Thread.sleep(60000);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			} catch (Exception e1) {
				LOGGER.error(e1);
				try {
					Thread.sleep(60000);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		}
	}

	private void process(Map<Long, Date> map) throws IOException, ReaperException, TimeoutException {
		if (null == map || map.size() == 0) {
			return;
		}
		Iterator<Long> i = map.keySet().iterator();
		List<ReaperTask> tasks = new ArrayList<ReaperTask>();
		while (i.hasNext()) {
			Long taskID = i.next();
			Date date = map.get(taskID);
			if (null == date) {
				date = new Date();
			}
			ReaperTask task = new ReaperTask();
			task.setID(taskID);
			task.setLastWorkTime(date.getTime() / 1000);
			tasks.add(task);
		}

		RabbitMQUtils.sendTask(Configure.getInstance(false).getProperty("mq_q_heartbeat"), tasks);
	}
}
