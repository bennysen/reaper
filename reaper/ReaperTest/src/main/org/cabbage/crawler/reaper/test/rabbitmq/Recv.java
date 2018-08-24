package org.cabbage.crawler.reaper.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Recv {

	private final static String QUEUE_NAME = "hello";

	public static void main(String[] argv) throws Exception {
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
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
				try {
					doWork(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					System.out.println("[x] Done");
					long tag = envelope.getDeliveryTag();// 该消息的index
					boolean multiple = false;// 是否批量,true:一次性ack所有小于等于tag的消息,false:只ack index为tag的消息
					channel.basicAck(tag, multiple);

				}
			}

			// 模拟执行任务的方法,一个点代表一秒
			private void doWork(String task) throws InterruptedException {
				for (char ch : task.toCharArray()) {
					if (ch == '.')
						Thread.sleep(1000);
				}
			}
		};
		// autoack改为false,打开manaul message ack
		// autoack 值为true代表只要发出的消息都自动有一个ack
		// 值false代表服务器会等待明确的ack,而不是自动返回的
		// 英文版:
		// true if the server should consider messages
		// * acknowledged once delivered;
		// false if the server should expect
		// * explicit acknowledgements
		boolean autoAck = false;
		String result = channel.basicConsume(QUEUE_NAME, autoAck, consumer);
		System.out.println("result:" + result);
	}
}