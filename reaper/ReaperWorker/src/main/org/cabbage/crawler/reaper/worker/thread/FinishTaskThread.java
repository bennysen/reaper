package org.cabbage.crawler.reaper.worker.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.worker.handle.ManagerHandle;

/**
 * 任务状态检测线程
 * 
 * 
 */
public class FinishTaskThread extends Thread {
	private static final Log LOGGER = LogFactory.getLog(FinishTaskThread.class);

	private ManagerHandle managerHandle;

	/**
	 * <默认构造函数>
	 * 
	 * @param managerHandle
	 *            managerHandle
	 * 
	 */
	public FinishTaskThread(ManagerHandle managerHandle) {
		this.managerHandle = managerHandle;
	}

	/**
	 * 
	 */
	public void run() {
		while (true) {
			LOGGER.info("    ResponseTaskThread is running ... ");
			try {
				process();
			} catch (Exception e) {
				LOGGER.error(e);
			}
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检查所有任务的状态
	 */
	private void process() {
		try {
			managerHandle.responseTask();
		} catch (Exception e) {
			LOGGER.error("ResponseTaskThread error!", e);
		}
	}
}
