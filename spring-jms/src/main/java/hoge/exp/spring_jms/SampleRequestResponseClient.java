package hoge.exp.spring_jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SampleRequestResponseClient {
    public static void main(String[] args) {
    	String brokerURL = "tcp://localhost:61616";
    	String requestDestinationName = "request";
    	String responseDestinationName = "response";
    	String message = "What time is it now?";

        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);

        Connection conn = null;
        try {
        	conn = cf.createConnection();

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
		} finally {
        	if (conn != null) {
        		try {
        			conn.close();
        		} catch (JMSException e) {
        			e.printStackTrace();
        		}
        	}
        }
    }
}
