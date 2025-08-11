package hoge.exp.activemq_jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.Destination;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;

public class SampleListener {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
		String queueName = "queue01";

		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL, username, password);
				JMSContext ctx = cf.createContext()) {

			Destination dest = ctx.createQueue(queueName);

			JMSConsumer consumer = ctx.createConsumer(dest);
			consumer.setMessageListener(msg -> {
				try {
					String body = msg.getBody(String.class);
					String corrId = msg.getJMSCorrelationID();
					System.out.println("Message: " + body + ", Correlation ID: " + corrId);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			});

			System.out.println("Message handler started. Press Ctrl+C to stop.");
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
