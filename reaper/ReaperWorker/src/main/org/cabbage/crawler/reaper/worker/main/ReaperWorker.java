package org.cabbage.crawler.reaper.worker.main;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.DateUtils;
import org.cabbage.commons.utils.file.FileUtils;
import org.cabbage.crawler.reaper.worker.thread.RequestTaskThread;
import org.cabbage.crawler.reaper.worker.utils.ReduceUtils;

/**
 * ReaperWorker入口
 * 
 * @author wkshen
 *
 */
public class ReaperWorker {

	private static final Log LOGGER = LogFactory.getLog(ReaperWorker.class);

	private static int tooManyOpenFileCount = 0;

	private static String DEVICE = null;

	public static String getDevice() {
		return DEVICE;
	}

	private static void trackerTask() {
		// TODO Auto-generated method stub

	}

	private static void responseStopTask() {
		// TODO Auto-generated method stub

	}

	private static void requestRunTask() {
		try {
			Thread thread = new RequestTaskThread();
			thread.start();
		} catch (Exception e1) {
			LOGGER.error("", e1);
		}
	}

	private static void checkAllTaskState() {
		// TODO Auto-generated method stub

	}

	private static void resetTask() {
		// TODO Auto-generated method stub

	}

	private static void mapdb() {
		ReduceUtils.mapdb();
	}

	private static void initialize() {
		// TODO Auto-generated method stub
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
		if (tooManyOpenFileCount > 1000) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized static int getTooManyOpenFileCount() {
		return tooManyOpenFileCount;
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
		int count = 0;
		return count;
	}

	public synchronized static int getCurrentStopTaskSize() {
		int count = 0;
		return count;
	}

	public static void main(String[] args) {
		// 1.1初始化配置文件，数据库连接，任务管理器
		initialize();

		mapdb();

		resetTask();

		// 1.3检查所有任务的状态
		checkAllTaskState();

		// 1.5任务管理器重任务表中提取任务
		requestRunTask();

		responseStopTask();

		trackerTask();
	}

}
