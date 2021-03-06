package org.cabbage.crawler.reaper.worker.main;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.DateUtils;
import org.cabbage.commons.utils.file.FileUtils;
import org.cabbage.crawler.reaper.exception.ReaperException;
import org.cabbage.crawler.reaper.worker.config.Configure;
import org.cabbage.crawler.reaper.worker.handle.ManagerHandle;
import org.cabbage.crawler.reaper.worker.thread.CheckTaskStateThread;
import org.cabbage.crawler.reaper.worker.thread.FinishTaskThread;
import org.cabbage.crawler.reaper.worker.thread.RequestTaskThread;
import org.cabbage.crawler.reaper.worker.thread.TaskHeartbeatThread;
import org.cabbage.crawler.reaper.worker.utils.RabbitMQUtils;
import org.cabbage.crawler.reaper.worker.utils.ReduceUtils;

/**
 * ReaperWorker入口
 * 
 * @author wkshen
 *
 */
public class ReaperWorker {

	private static final Log LOGGER = LogFactory.getLog(ReaperWorker.class);

	private static int TOO_MANY_OPEN_FILES_COUNT = 0;

	private static int ERROR_COUNT = 0;

	private static String DEVICE = null;

	public static String getDevice() {
		return DEVICE;
	}

	private static void responseStopTask() {
		try {
			Thread thread = new FinishTaskThread(ManagerHandle.getInstance());
			thread.start();
		} catch (Exception e1) {
			LOGGER.error("", e1);
		}
	}

	private static void heartbeat() {
		try {
			Thread checkThread = new TaskHeartbeatThread();
			checkThread.start();
		} catch (Exception e1) {
			LOGGER.error("", e1);
		}
	}

	private static void requestRunTask() {
		Integer maxTaskCount = 16;
		try {
			maxTaskCount = Configure.getInstance(false).getPropertyInteger("maxTaskCount");
			if (null == maxTaskCount || maxTaskCount < 1) {
				maxTaskCount = 16;
			}
		} catch (ReaperException e) {
			LOGGER.error("Please check configure file,maxTaskCount is invalid!");
		}

		try {
			Thread thread = new RequestTaskThread(maxTaskCount);
			thread.start();
		} catch (Exception e1) {
			LOGGER.error("", e1);
		}
	}

	/**
	 * 检查所有任务的状态
	 */
	private static void checkAllTaskState() {
		try {
			Thread checkThread = new CheckTaskStateThread(ManagerHandle.getInstance());
			checkThread.start();
		} catch (Exception e1) {
			LOGGER.error("", e1);
		}
	}

	private static void resetTask() {
		try {
			String resetQueue = Configure.getInstance(false).getProperty("mq_q_reset");
			RabbitMQUtils.send(resetQueue, getDevice());
		} catch (IOException e) {
			LOGGER.error("", e);
		} catch (ReaperException e) {
			LOGGER.error("", e);
		} catch (TimeoutException e) {
			LOGGER.error("", e);
		}
	}

	private static void mapdb() {
		ReduceUtils.initMapDB();
	}

	private static void initialize() {
		initializeDevice();
	}

	private static void initializeDevice() {
		try {
			DEVICE = (InetAddress.getLocalHost()).getHostName();
		} catch (UnknownHostException e) {
			DEVICE = "UNKNOWN-HOST";
			LOGGER.warn("InetAddress get host name error!", e);
		}
	}

	public synchronized static boolean isTooManyOpenFile() {
		if (TOO_MANY_OPEN_FILES_COUNT > 1000) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized static void tooManyOpenFileCount() {
		TOO_MANY_OPEN_FILES_COUNT++;
	}

	public synchronized static void terminate(String msg) {
		try {
			boolean is = FileUtils.isExist("terminateLog.log");
			if (is) {
				append("terminateLog.log", DateUtils.getCurrDateTime() + " : " + msg + "\r\n");
			} else {
				FileUtils.writeTxtFile("terminateLog.log", DateUtils.getCurrDateTime() + " : " + msg + "\r\n");
			}
		} catch (IOException e) {
			LOGGER.error(e);
		} finally {
			LOGGER.warn("System is terminated ![" + msg + "]");
			System.exit(-1);
		}
	}

	private static void append(String fileName, String content) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName, true);
			writer.write(content);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}

	public synchronized static int getCurrentTaskSize() {
		int count = ManagerHandle.getInstance().getRunTasks().size();
		return count;
	}

	public synchronized static int getCurrentStopTaskSize() {
		int count = ManagerHandle.getInstance().getStopTasks().size();
		return count;
	}

	public synchronized static boolean isErrorCountFull() {
		if (ERROR_COUNT > 300) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized static void errorCount() {
		ERROR_COUNT++;
	}

	public static void main(String[] args) {
		// 1.1初始化配置文件，数据库连接，任务管理器
		initialize();
		
		resetTask();

		mapdb();

		// 1.3检查所有任务的状态
		checkAllTaskState();

		// 1.5任务管理器重任务表中提取任务
		requestRunTask();

		responseStopTask();

		heartbeat();
	}

}
