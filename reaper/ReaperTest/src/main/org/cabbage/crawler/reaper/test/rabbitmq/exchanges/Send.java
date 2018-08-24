package org.cabbage.crawler.reaper.test.rabbitmq.exchanges;
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

        //exchange的类型包括:direct, topic, headers and fanout,我们本例子主要关注的是fanout
        //fanout类型是指向所有的队列发送消息
        //以下是创建一个fanout类型的exchange,取名logs
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        String message = getMessage(argv);

        //1.在上个"hello world"例子中,我们用的是channel.basicPublish("", "hello", null, message.getBytes());
        //这里用了默认的exchanges,一个空字符串 "",在basicPublish这个方法中,第一个参数即是exchange的名称
        //2.准备向我们命名的exchange发送消息啦
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }

    private static String getMessage(String[] strings){
        if (strings.length < 1)
            return "info: Hello World!";
        return joinStrings(strings, " ");
    }

    private static String joinStrings(String[] strings, String delimiter) {
        int length = strings.length;
        if (length == 0) return "";
        StringBuilder words = new StringBuilder(strings[0]);
        for (int i = 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
}
