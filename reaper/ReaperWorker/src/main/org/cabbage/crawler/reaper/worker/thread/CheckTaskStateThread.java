package org.cabbage.crawler.reaper.worker.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.worker.handle.ManagerHandle;
import org.cabbage.crawler.reaper.worker.main.ReaperWorker;

/**
 * 任务状态检测线程
 * 
 * 
 */
public class CheckTaskStateThread extends Thread {

	private static final Log LOGGER = LogFactory.getLog(CheckTaskStateThread.class);

	private ManagerHandle managerHandle;

	/**
	 * 
	 * @param managerHandle
	 */
	public CheckTaskStateThread(ManagerHandle managerHandle) {
		this.managerHandle = managerHandle;
	}

	/**
	 * 
	 */
	public void run() {
		if (managerHandle == null) {
			LOGGER.error("managerHandle is invalid! System exit!");
			ReaperWorker.terminate("managerHandle is invalid! System exit!");
			return;
		}
		while (true) {
			LOGGER.info("................. CheckTaskStateThread is alive!");
			if (ReaperWorker.isErrorCountFull()) {
				ReaperWorker.terminate("ERROR_COUNT is full!");
			} else if (ReaperWorker.isTooManyOpenFile()) {
				ReaperWorker.terminate("TOO_MANY_OPEN_FILES_COUNT is full!");

			}
			LOGGER.info("    CheckTaskStateThread is running ... ");
			try {
				checkAllTaskState();
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
	private void checkAllTaskState() {
		try {
			managerHandle.checkAllTaskState();
		} catch (Exception e) {
			LOGGER.error("Check task state error", e);
		}
	}

}
