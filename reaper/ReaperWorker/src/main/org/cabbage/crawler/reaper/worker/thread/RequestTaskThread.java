package org.cabbage.crawler.reaper.worker.thread;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.worker.handle.ManagerHandle;
import org.cabbage.crawler.reaper.worker.main.ReaperWorker;


/**
 * 任务管理器从任务队列中提取任务
 * 
 * 
 */
public class RequestTaskThread extends Thread {
	private static final Log LOGGER = LogFactory
			.getLog(RequestTaskThread.class);

	/**
	 * <默认构造函数>
	 * 
	 * @param managerHandles
	 *            managerHandles
	 * 
	 */
	public RequestTaskThread() {
	}

	public void run() {
		Integer taskCount = null;
		int selectTaskWait = 0;
		int maxTaskCount = 0;
		// boolean isSameCount = false;
		while (true) {

			if (ReaperWorker.isTooManyOpenFile()) {
				ReaperWorker.terminate("tooManyOpenFileCount["
						+ ReaperWorker.getTooManyOpenFileCount()
						+ "]");
			}
			LOGGER.warn("tooManyOpenFileCount["
					+ ReaperWorker.getTooManyOpenFileCount() + "]");
			try {
				int currentTotalTaskSize = ReaperWorker
						.getCurrentTaskSize();
				int currentStopTaskSize = ReaperWorker
						.getCurrentStopTaskSize();
				LOGGER
						.info("\r\n                           Total run Task >>>>>>>> 【"
								+ currentTotalTaskSize
								+ "】。\r\n                          Total stop Task >>>>>>>> 【"
								+ currentStopTaskSize + "】。\r\n");

				if (currentTotalTaskSize >= maxTaskCount) {
					LOGGER.warn("采集任务已经达到最大阀值，如需增加请修改netpf-crawler.xml配置。");
					try {
						Thread.sleep(selectTaskWait);
					} catch (Exception e) {
						LOGGER.error("", e);
					}
					continue;
				}

					int free = maxTaskCount - currentTotalTaskSize;
					// ///////////////////////////////////////////////////////////////////
					if (free == 0 || free < 0) {
						break;
					}
					LOGGER.info("本次获取任务数量【" + free + "】。");
					
					ManagerHandle handle = new ManagerHandle();
					
					//TODO get task list
					List<ReaperTask> ts = null;
					
					
					
					
					
					if (null == ts || ts.size() == 0) {
					} else {
						for (int i = 0; i < ts.size(); i++) {
							ReaperTask task = ts.get(i);
							if (null != task) {
								LOGGER.info("run task [" + i + "],");
								try {
									handle.runTask(task);
								} catch (Exception e) {
									LOGGER.error(e);
									handle.stopTask(task);
								}
							} else {
								LOGGER.warn("No task to run[" + i + "]");
							}
						}
					}
				try {
					Thread.sleep(selectTaskWait * 1L);
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			} catch (Exception e1) {
				LOGGER.error("", e1);
			}
		}
	}
}
