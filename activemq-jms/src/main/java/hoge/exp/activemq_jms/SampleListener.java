package hoge.exp.activemq_jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class SampleListener {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
		String queueName = "queue01";

		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
				Connection conn = cf.createConnection(username, password)) {
			Session session = conn.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			Destination dest = session.createQueue(queueName);

			MessageConsumer consumer = session.createConsumer(dest);
			consumer.setMessageListener(message -> {
				try {
					TextMessage textMessage = (TextMessage) message;
					System.out.println("Message: " + textMessage.getText() +
							", Correlation ID: " + textMessage.getJMSCorrelationID());
				} catch (JMSException e) {
					e.printStackTrace();
				}
			});

			conn.start();

    		System.out.println("Message handler started. Press Ctrl+C to stop.");
    		Thread.currentThread().join();
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
