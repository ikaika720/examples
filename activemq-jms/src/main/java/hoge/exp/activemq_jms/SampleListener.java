package hoge.exp.activemq_jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class SampleListener implements MessageListener {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String queueName = "queue01";

        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);

        try (Connection conn = cf.createConnection()) {
			Session session = conn.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			Destination dest = session.createQueue(queueName);

			MessageConsumer consumer = session.createConsumer(dest);
			consumer.setMessageListener(new SampleListener());

			conn.start();

			Thread.sleep(100);
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			System.out.println(((TextMessage) message).getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
