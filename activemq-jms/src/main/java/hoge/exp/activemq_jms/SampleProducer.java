package hoge.exp.activemq_jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.TextMessage;

public class SampleProducer {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
		String queueName = "queue01";
		String message = "hello";
		int numOfMessages = 1000;

		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL, username, password);
			 JMSContext ctx = cf.createContext()) {

			Destination dest = ctx.createQueue(queueName);

			JMSProducer producer = ctx.createProducer();
			for (int i = 0; i < numOfMessages; i++) {
				TextMessage textMessage = ctx.createTextMessage(message + " #" + i);
				producer.setJMSCorrelationID("CORR-" + i);
				producer.send(dest, textMessage);
			}
		}
	}
}
