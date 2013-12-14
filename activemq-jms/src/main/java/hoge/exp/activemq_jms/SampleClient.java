package hoge.exp.activemq_jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SampleClient {
    public static void main(String[] args) {
    	String brokerURL = "tcp://localhost:61616";
    	String queueName = "queue01";
    	String message = "hello";

        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);

        Connection conn = null;
        try {
        	conn = cf.createConnection();

        	Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

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
