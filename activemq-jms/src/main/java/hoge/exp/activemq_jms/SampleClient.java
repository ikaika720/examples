package hoge.exp.activemq_jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.Destination;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;

public class SampleClient {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
		String queueName = "queue01";
		String message = "hello";

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL, username, password);
			JMSContext cxt = cf.createContext()) {

			Destination dest = cxt.createQueue(queueName);

			JMSProducer producer = cxt.createProducer();
			producer.send(cxt.createQueue(queueName), message);

			JMSConsumer consumer = cxt.createConsumer(dest);

			String body = consumer.receiveBody(String.class, 1000L);
			System.out.println(body);
		}
	}
}
