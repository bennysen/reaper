package org.cabbage.crawler.reaper.test.rabbitmq.routing;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Send {
	private static final String EXCHANGE_NAME = "x";

	public static void main(String[] argv) throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.80.101");
		factory.setPort(5672);
		factory.setUsername("admin");
		factory.setPassword("123456");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		// exchange的类型包括:direct, topic, headers and fanout,我们本例子主要关注的是fanout
		// fanout类型是指向所有的队列发送消息
		// 以下是创建一个fanout类型的exchange,取名logs
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

		String severity = "error";
		String message = severity + ":This is a " + severity + " message!";

		channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
		System.out.println(" [x] Sent '" + severity + "':'" + message + "'");

		channel.close();
		connection.close();
	}

}
