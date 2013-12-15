package hoge.exp.activemq_jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SampleListener implements MessageListener {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String queueName = "queue01";

		ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
		try {
			final Connection conn = cf.createConnection();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						conn.close();
						System.out.println(
								"The connection was successfully closed.");
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});

			Session session = conn.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			Destination dest = session.createQueue(queueName);

			MessageConsumer consumer = session.createConsumer(dest);
			consumer.setMessageListener(new SampleListener());

			conn.start();
		} catch (JMSException e) {
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
