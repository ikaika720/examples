package hoge.exp.spring_jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class SampleRequestResponseClient {
    public static void main(String[] args) {
    	String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
    	String requestDestinationName = "request";
    	String responseDestinationName = "response";
    	String message = "What time is it now?";

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
			Connection conn = cf.createConnection(username, password)) {
        	Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        	MessageProducer producer = session.createProducer(
        			session.createQueue(requestDestinationName));

        	producer.send(session.createTextMessage(message));

        	MessageConsumer consumer = session.createConsumer(
        			session.createQueue(responseDestinationName));

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
