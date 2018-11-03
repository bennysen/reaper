package org.cabbage.crawler.reaper.worker.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.worker.thread.ReaperWorkerThread;

/**
 * 
 * @author wkshen
 *
 */
public class WorkerHandle {

	private static final Log LOGGER = LogFactory.getLog(WorkerHandle.class);

	ReaperTask task = null;

	ReaperWorkerThread thread = null;
	
	public WorkerHandle(ReaperTask task) {
		this.task = task;
		thread = new ReaperWorkerThread(task);
	}

	public ReaperTask getTask() {
		return task;
	}

	public Long getWorkerState() {
		return thread.getWorkerState();
	}

	public void stopTask() {
		try {
			if (thread.isAlive()) {
				thread.interrupt();
			}
		} catch (Exception e) {
			if (null == task) {
				LOGGER.error("Stop task[null] error!", e);
			} else {
				LOGGER.error("Stop task[" + task.getID() + "] error!", e);
			}
		}
	}

	public void setTask(ReaperTask task) {
		this.task = task;
	}

	public void runTask() {
		thread.start();
	}
	
	public boolean isAlive() {
		return thread.isFinished();
	}
}
