package hoge.exp.spring_jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SampleTopicPublisher {
    public static void main(String[] args) {
    	String brokerURL = "tcp://localhost:61616";
    	String destinationName = "topic01";
    	String message = "hello";

        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);

        Connection conn = null;
        try {
        	conn = cf.createConnection();

        	Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        	Destination dest = session.createTopic(destinationName);

        	MessageProducer producer = session.createProducer(dest);
        	producer.send(session.createTextMessage(message));
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
