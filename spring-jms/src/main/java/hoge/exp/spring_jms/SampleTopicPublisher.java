package hoge.exp.spring_jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;

public class SampleTopicPublisher {
    public static void main(String[] args) {
    	String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
    	String destinationName = "topic01";
    	String message = "hello";
		int numOfMessages = 1000;

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL, username, password);
			Connection conn = cf.createConnection()) {
        	Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        	Destination dest = session.createTopic(destinationName);

        	MessageProducer producer = session.createProducer(dest);
			for (int i = 0; i < numOfMessages; i++) {
        		producer.send(session.createTextMessage(message + " " + i));
			}
        } catch (JMSException e) {
			e.printStackTrace();
        }
    }
}
