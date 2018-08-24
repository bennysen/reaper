package org.cabbage.crawler.reaper.test.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class Send {

	private final static String QUEUE_NAME = "hello";

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.80.101");
		factory.setPort(5672);
		factory.setUsername("admin");
		factory.setPassword("123456");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		// 是否持久的;耐用的，耐久的;长期的
		// 声明队列为持久类型的,声明的时候记得把队列的名字改一下,因为rmq不允许对一个已经存在的队列重新定义
		boolean durable = true;
		channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
		/**
		 * 使用basicQos方法和 prefetchCount = 1设置。 这告诉RabbitMQ一次不要向消费者发送多个消息。
		 * 或者换句话说，不要向消费者发送新消息，直到它处理并确认了前一个消息。 相反，它会将其分派给不是仍然忙碌的下一个消费者。
		 */
		int prefetchCount = 1;
		// 代表让服务器不要同时给一个消费者超过1个消息,直到当前的消息被消耗掉
		channel.basicQos(prefetchCount);

		String message = "Hello World!...........................................";
		// MessageProperties.PERSISTENT_TEXT_PLAIN 配合durable=true使用
		channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
		System.out.println(" [x] Sent '" + message + "'");

		channel.close();
		connection.close();
	}

}
