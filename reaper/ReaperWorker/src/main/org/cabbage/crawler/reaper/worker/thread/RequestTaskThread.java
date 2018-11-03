package org.cabbage.crawler.reaper.worker.thread;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.exception.ReaperException;
import org.cabbage.crawler.reaper.worker.config.Configure;
import org.cabbage.crawler.reaper.worker.handle.ManagerHandle;
import org.cabbage.crawler.reaper.worker.main.ReaperWorker;
import org.cabbage.crawler.reaper.worker.utils.RabbitMQUtils;

/**
 * 任务管理器从任务队列中提取任务
 * 
 * 
 */
public class RequestTaskThread extends Thread {

	private static final Log LOGGER = LogFactory.getLog(RequestTaskThread.class);

	int maxTaskCount = 16;
	int selectTaskWait = 10000;

	/**
	 * 
	 * @param maxTaskCount
	 */
	public RequestTaskThread(int maxTaskCount) {
		this.maxTaskCount = maxTaskCount;
		try {
			Integer selectTaskWait = Configure.getInstance(false).getPropertyInteger("selectTaskWait");
			if (null == selectTaskWait || selectTaskWait < 10000) {
				// if selectTaskWait < 10000 then use default value 10000
			} else {
				this.selectTaskWait = selectTaskWait;
			}
		} catch (ReaperException e) {
		}
	}

	public void run() {

		while (true) {
			try {
				int currentTotalTaskSize = ReaperWorker.getCurrentTaskSize();
				int currentStopTaskSize = ReaperWorker.getCurrentStopTaskSize();
				LOGGER.info("\r\n                           Total run Task >>>>>>>> 【" + currentTotalTaskSize
						+ "】。\r\n                          Total stop Task >>>>>>>> 【" + currentStopTaskSize
						+ "】。\r\n");

				if (currentTotalTaskSize >= maxTaskCount) {
					LOGGER.warn("采集任务已经达到最大阀值，如需增加请修改reaperWorker.xml配置。");
					try {
						Thread.sleep(selectTaskWait);
					} catch (Exception e) {
						LOGGER.error("", e);
					}
					continue;
				}

				int free = maxTaskCount - currentTotalTaskSize;
				
				if (free == 0 || free < 0) {
					break;
				}
				LOGGER.info("本次获取任务数量【" + free + "】。");

				ManagerHandle handle = ManagerHandle.getInstance();

				List<ReaperTask> ts = RabbitMQUtils.getTask(free);

				if (null == ts || ts.size() == 0) {
				} else {
					for (int i = 0; i < ts.size(); i++) {
						ReaperTask task = ts.get(i);
						if (null == task||task.isInvalid()) {
							LOGGER.warn("No task to run[" + i + "]");
						} else {
							LOGGER.info("run task [" + i + "],");
							try {
								handle.runTask(task);
							} catch (Exception e) {
								LOGGER.error(e);
								handle.stopTask(task);
							}
						}
					}
				}
				try {
					Thread.sleep(selectTaskWait);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			} catch (Exception e1) {
				LOGGER.error(e1);
			}
		}
	}
}
