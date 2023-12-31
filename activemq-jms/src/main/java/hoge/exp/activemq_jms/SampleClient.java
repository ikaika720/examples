package hoge.exp.activemq_jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class SampleClient {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String queueName = "queue01";
		String message = "hello";

        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);

        try (Connection conn = cf.createConnection()) {
			Session session = conn.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			Destination dest = session.createQueue(queueName);

			MessageProducer producer = session.createProducer(dest);
			producer.send(session.createTextMessage(message));

			MessageConsumer consumer = session.createConsumer(dest);

			conn.start();

			TextMessage received = (TextMessage) consumer.receive(1000L);
			if (received != null) {
				System.out.println(received.getText());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
