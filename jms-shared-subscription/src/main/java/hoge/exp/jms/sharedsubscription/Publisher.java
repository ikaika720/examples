package hoge.exp.jms.sharedsubscription;

import static java.util.stream.IntStream.range;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class Publisher {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
		String topicString = "topic01";
		String message = "hello";
		int numOfMessages = 100;
		int numOfThreads = 2;

		try (var cf = new ActiveMQConnectionFactory(brokerURL, username, password)) {
			range(0, numOfThreads).parallel().forEach(i -> {
				int thradId = i;

				try (var ctx = cf.createContext()) {
					var topic = ctx.createTopic(topicString);
					var producer = ctx.createProducer();
					range(0, numOfMessages).forEach(j -> {
						producer.send(topic, message + " " + thradId + "-" + j);
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
}
