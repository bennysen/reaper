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

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
		    @Override
		    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
		            throws IOException {
		        String message = new String(body, "UTF-8");
		        System.out.println(" [x] Received '" + message + "'");
		        try{
		            doWork(message);
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        } finally {
		            System.out.println("[x] Done");
		            long tag = envelope.getDeliveryTag();//该消息的index
		            boolean multiple = false;//是否批量,true:一次性ack所有小于等于tag的消息,false:只ack index为tag的消息
		            channel.basicAck(tag, multiple);

		        }
		    }

		    //模拟执行任务的方法,一个点代表一秒
		    private void doWork(String task) throws InterruptedException {
		        for (char ch: task.toCharArray()) {
		            if (ch == '.') Thread.sleep(1000);
		        }
		    }
		};
		boolean autoAck = false; // acknowledgment is covered below
		String result = channel.basicConsume(QUEUE_NAME, autoAck, consumer);
		System.out.println("result:" + result);
	}
}