package hoge.exp.spring_jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;

public class SampleClient {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
		String queueName = "queue01";
		String message = "hello";
		int numOfMessages = 1000;

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
			Connection conn = cf.createConnection(username, password)) {
			Session session = conn.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			Destination dest = session.createQueue(queueName);

			MessageProducer producer = session.createProducer(dest);
			for (int i = 0; i < numOfMessages; i++) {
				producer.send(session.createTextMessage(message));
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
