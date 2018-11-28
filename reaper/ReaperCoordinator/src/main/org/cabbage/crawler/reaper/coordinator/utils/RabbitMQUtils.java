package org.cabbage.crawler.reaper.coordinator.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.bean.SerializableUtils;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.coordinator.conf.Configure;
import org.cabbage.crawler.reaper.exception.ReaperException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;

public class RabbitMQUtils {

	private static final Log LOGGER = LogFactory.getLog(RabbitMQUtils.class);

	public static synchronized List<String> getResetHost(int number)
			throws IOException, TimeoutException, ReaperException {
		LOGGER.info("getTask begin...");
		List<String> hosts = new ArrayList<String>();
		String mqHost = Configure.getInstance(false).getProperty("mq_host");
		Integer mqPort = Configure.getInstance(false).getPropertyInteger("mq_port");
		String mqUsername = Configure.getInstance(false).getProperty("mq_username");
		String mqPassword = Configure.getInstance(false).getProperty("mq_password");
		String mqResetQueueName = Configure.getInstance(false).getProperty("mq_q_reset");

		if (null == mqHost || mqHost.trim().length() == 0 || null == mqPort || mqPort < 0 || null == mqUsername
				|| mqUsername.trim().length() == 0 || null == mqPassword || mqPassword.trim().length() == 0
				|| null == mqResetQueueName || mqResetQueueName.trim().length() == 0) {
			LOGGER.error("Check configure file,MQ configures are invalid!");
			return null;
		} else {
			LOGGER.info("MQ configures for get reset host[" + mqHost + ":" + mqPort + "/" + mqResetQueueName + "{"
					+ mqUsername + "/" + mqPassword + "}]");
		}
		ConnectionFactory factory = null;
		Connection connection = null;
		Channel channel4get = null;

		try {
			factory = new ConnectionFactory();
			factory.setHost(mqHost);
			factory.setPort(mqPort);
			factory.setUsername(mqUsername);
			factory.setPassword(mqPassword);

			connection = factory.newConnection();
			channel4get = connection.createChannel();

			////////////////////////////////////////////////////
			int count = 0;
			while (true) {
				count++;
				if (hosts.size() >= number || count > 10) {
					break;
				}
				GetResponse response = channel4get.basicGet(mqResetQueueName, false);
				if (null == response) {
					try {
						Thread.sleep(3000l);
					} catch (InterruptedException e) {
					}
					continue;
				}
				String host = null;
				try {
					host = (String) SerializableUtils.readObject(response.getBody());
					if (null == host || host.trim().length() == 0) {
					} else {
						hosts.add(host);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}

				channel4get.basicAck(response.getEnvelope().getDeliveryTag(), false);
			}
		} finally {
			if (null != channel4get && channel4get.isOpen()) {
				channel4get.close();
			}
			if (null != connection && connection.isOpen()) {
				connection.close();
			}
		}
		LOGGER.info("getTask end!");
		return hosts;
	}

	public static synchronized void sendTask(String queue, List<ReaperTask> tasks)
			throws IOException, ReaperException, TimeoutException {
		if (null == tasks || tasks.size() == 0) {
			return;
		}
		String mqHost = Configure.getInstance(false).getProperty("mq_host");
		Integer mqPort = Configure.getInstance(false).getPropertyInteger("mq_port");
		String mqUsername = Configure.getInstance(false).getProperty("mq_username");
		String mqPassword = Configure.getInstance(false).getProperty("mq_password");

		if (null == mqHost || mqHost.trim().length() == 0 || null == mqPort || mqPort < 0 || null == mqUsername
				|| mqUsername.trim().length() == 0 || null == mqPassword || mqPassword.trim().length() == 0
				|| null == queue || queue.trim().length() == 0) {
			LOGGER.error("Check configure file,MQ configures are invalid!");
		} else {
			LOGGER.info("MQ configures for get ReaperTask[" + mqHost + ":" + mqPort + "/" + queue + "{" + mqUsername
					+ "/" + mqPassword + "}]");
		}

		ConnectionFactory factory = null;
		Connection connection = null;
		Channel channel = null;
		try {
			factory = new ConnectionFactory();
			factory.setHost(mqHost);
			factory.setPort(mqPort);
			factory.setUsername(mqUsername);
			factory.setPassword(mqPassword);

			connection = factory.newConnection();
			channel = connection.createChannel();

			// 是否持久的;耐用的，耐久的;长期的
			// 声明队列为持久类型的,声明的时候记得把队列的名字改一下,因为rmq不允许对一个已经存在的队列重新定义
			boolean durable = true;
			channel.queueDeclare(queue, durable, false, false, null);
			/**
			 * 使用basicQos方法和 prefetchCount = 1设置。 这告诉RabbitMQ一次不要向消费者发送多个消息。
			 * 或者换句话说，不要向消费者发送新消息，直到它处理并确认了前一个消息。 相反，它会将其分派给不是仍然忙碌的下一个消费者。
			 */
			int prefetchCount = 1;
			// 代表让服务器不要同时给一个消费者超过1个消息,直到当前的消息被消耗掉
			channel.basicQos(prefetchCount);

			for (ReaperTask task : tasks) {
				if (null == task || null == task.getID() || null == task.getURL()
						|| 0 == task.getURL().trim().length()) {
					continue;
				}

				// MessageProperties.PERSISTENT_TEXT_PLAIN 配合durable=true使用
				channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN,
						SerializableUtils.writeObject(task));
				LOGGER.info("Send [" + task.getID() + ":" + task.getURL() + "]");
			}
		} finally {
			if (null != channel && channel.isOpen()) {
				channel.close();
			}
			if (null != connection && connection.isOpen()) {
				connection.close();
			}
		}
	}

	public static synchronized List<ReaperTask> getFinishedTask(int number)
			throws IOException, TimeoutException, ReaperException {
		LOGGER.info("getTask begin...");
		List<ReaperTask> tasks = new ArrayList<ReaperTask>();
		String mqHost = Configure.getInstance(false).getProperty("mq_host");
		Integer mqPort = Configure.getInstance(false).getPropertyInteger("mq_port");
		String mqUsername = Configure.getInstance(false).getProperty("mq_username");
		String mqPassword = Configure.getInstance(false).getProperty("mq_password");
		String queue = Configure.getInstance(false).getProperty("mq_q_finished");

		if (null == mqHost || mqHost.trim().length() == 0 || null == mqPort || mqPort < 0 || null == mqUsername
				|| mqUsername.trim().length() == 0 || null == mqPassword || mqPassword.trim().length() == 0
				|| null == queue || queue.trim().length() == 0) {
			LOGGER.error("Check configure file,MQ configures are invalid!");
			return null;
		} else {
			LOGGER.info("MQ configures for get Finished task[" + mqHost + ":" + mqPort + "/" + queue + "{" + mqUsername
					+ "/" + mqPassword + "}]");
		}
		ConnectionFactory factory = null;
		Connection connection = null;
		Channel channel4get = null;

		try {
			factory = new ConnectionFactory();
			factory.setHost(mqHost);
			factory.setPort(mqPort);
			factory.setUsername(mqUsername);
			factory.setPassword(mqPassword);

			connection = factory.newConnection();
			channel4get = connection.createChannel();

			////////////////////////////////////////////////////
			int count = 0;
			while (true) {
				count++;
				if (tasks.size() >= number || count > 10) {
					break;
				}
				GetResponse response = channel4get.basicGet(queue, false);
				if (null == response) {
					try {
						Thread.sleep(3000l);
					} catch (InterruptedException e) {
					}
					continue;
				}
				ReaperTask task = null;
				try {
					task = (ReaperTask) SerializableUtils.readObject(response.getBody());
					if (null == task || task.isInvalid()) {
					} else {
						tasks.add(task);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}

				channel4get.basicAck(response.getEnvelope().getDeliveryTag(), false);
			}
		} finally {
			if (null != channel4get && channel4get.isOpen()) {
				channel4get.close();
			}
			if (null != connection && connection.isOpen()) {
				connection.close();
			}
		}
		LOGGER.info("getTask end!");
		return tasks;
	}

	public static synchronized List<ReaperTask> getProcessingTask(int number)
			throws IOException, TimeoutException, ReaperException {
		LOGGER.info("getTask begin...");
		List<ReaperTask> tasks = new ArrayList<ReaperTask>();
		String mqHost = Configure.getInstance(false).getProperty("mq_host");
		Integer mqPort = Configure.getInstance(false).getPropertyInteger("mq_port");
		String mqUsername = Configure.getInstance(false).getProperty("mq_username");
		String mqPassword = Configure.getInstance(false).getProperty("mq_password");
		String queue = Configure.getInstance(false).getProperty("mq_q_processing");

		if (null == mqHost || mqHost.trim().length() == 0 || null == mqPort || mqPort < 0 || null == mqUsername
				|| mqUsername.trim().length() == 0 || null == mqPassword || mqPassword.trim().length() == 0
				|| null == queue || queue.trim().length() == 0) {
			LOGGER.error("Check configure file,MQ configures are invalid!");
			return null;
		} else {
			LOGGER.info("MQ configures for get Processing Task[" + mqHost + ":" + mqPort + "/" + queue + "{"
					+ mqUsername + "/" + mqPassword + "}]");
		}
		ConnectionFactory factory = null;
		Connection connection = null;
		Channel channel4get = null;

		try {
			factory = new ConnectionFactory();
			factory.setHost(mqHost);
			factory.setPort(mqPort);
			factory.setUsername(mqUsername);
			factory.setPassword(mqPassword);

			connection = factory.newConnection();
			channel4get = connection.createChannel();

			////////////////////////////////////////////////////
			int count = 0;
			while (true) {
				count++;
				if (tasks.size() >= number || count > 10) {
					break;
				}
				GetResponse response = channel4get.basicGet(queue, false);
				if (null == response) {
					try {
						Thread.sleep(3000l);
					} catch (InterruptedException e) {
					}
					continue;
				}
				ReaperTask task = null;
				try {
					task = (ReaperTask) SerializableUtils.readObject(response.getBody());
					if (null == task || task.isInvalid()) {
					} else {
						tasks.add(task);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}

				channel4get.basicAck(response.getEnvelope().getDeliveryTag(), false);
			}
		} finally {
			if (null != channel4get && channel4get.isOpen()) {
				channel4get.close();
			}
			if (null != connection && connection.isOpen()) {
				connection.close();
			}
		}
		LOGGER.info("getTask end!");
		return tasks;
	}

	public static synchronized List<ReaperTask> checkTaskHeartbeat(int number)
			throws IOException, TimeoutException, ReaperException {
		LOGGER.info("getTask begin...");
		List<ReaperTask> tasks = new ArrayList<ReaperTask>();
		String mqHost = Configure.getInstance(false).getProperty("mq_host");
		Integer mqPort = Configure.getInstance(false).getPropertyInteger("mq_port");
		String mqUsername = Configure.getInstance(false).getProperty("mq_username");
		String mqPassword = Configure.getInstance(false).getProperty("mq_password");
		String mqResetQueueName = Configure.getInstance(false).getProperty("mq_q_heartbeat");

		if (null == mqHost || mqHost.trim().length() == 0 || null == mqPort || mqPort < 0 || null == mqUsername
				|| mqUsername.trim().length() == 0 || null == mqPassword || mqPassword.trim().length() == 0
				|| null == mqResetQueueName || mqResetQueueName.trim().length() == 0) {
			LOGGER.error("Check configure file,MQ configures are invalid!");
			return null;
		} else {
			LOGGER.info("MQ configures for get reset host[" + mqHost + ":" + mqPort + "/" + mqResetQueueName + "{"
					+ mqUsername + "/" + mqPassword + "}]");
		}
		ConnectionFactory factory = null;
		Connection connection = null;
		Channel channel4get = null;

		try {
			factory = new ConnectionFactory();
			factory.setHost(mqHost);
			factory.setPort(mqPort);
			factory.setUsername(mqUsername);
			factory.setPassword(mqPassword);

			connection = factory.newConnection();
			channel4get = connection.createChannel();

			////////////////////////////////////////////////////
			int count = 0;
			while (true) {
				count++;
				if (tasks.size() >= number || count > 10) {
					break;
				}
				GetResponse response = channel4get.basicGet(mqResetQueueName, false);
				if (null == response) {
					try {
						Thread.sleep(3000l);
					} catch (InterruptedException e) {
					}
					continue;
				}
				ReaperTask task = null;
				try {
					task = (ReaperTask) SerializableUtils.readObject(response.getBody());
					if (null == task || task.isInvalid()) {
					} else {
						tasks.add(task);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}

				channel4get.basicAck(response.getEnvelope().getDeliveryTag(), false);
			}
		} finally {
			if (null != channel4get && channel4get.isOpen()) {
				channel4get.close();
			}
			if (null != connection && connection.isOpen()) {
				connection.close();
			}
		}
		LOGGER.info("getTask end!");
		return tasks;
	}

	public static void main(String[] args) throws IOException, TimeoutException, ReaperException {
		List<ReaperTask> tasks = new ArrayList<ReaperTask>();
		ReaperTask task = new ReaperTask();
		task.setID(System.currentTimeMillis());
		task.setURL("http://www.163.com/");
		task.setLastWorkTime(System.currentTimeMillis() / 1000);
		tasks.add(task);

		RabbitMQUtils.sendTask("waitting4process", tasks);

	}
}
