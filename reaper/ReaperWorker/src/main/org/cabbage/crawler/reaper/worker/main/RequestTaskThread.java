package org.cabbage.crawler.reaper.worker.main;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 任务管理器重任务表中提取任务
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

	/**
	 * {@inheritDoc}
	 */
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
						Thread.sleep(selectTaskWait * 1L);
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
					currentTotalTaskSize = currentTotalTaskSize + free;

					LOGGER.info("本次获取任务数量【" + free + "】。");
					List<?> ts = null;
					if (null == ts || ts.size() == 0) {
					} else {
						for (int i = 0; i < ts.size(); i++) {
							Object task = ts.get(i);
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
