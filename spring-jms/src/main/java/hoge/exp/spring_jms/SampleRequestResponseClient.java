package hoge.exp.spring_jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;

public class SampleRequestResponseClient {
    public static void main(String[] args) {
    	String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
    	String requestDestinationName = "request";
    	String responseDestinationName = "response";
    	String message = "What time is it now?";

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL, username, password);
			JMSContext ctx = cf.createContext()) {

        	ctx.createProducer().send(ctx.createQueue(requestDestinationName), message);

			JMSConsumer consumer = ctx.createConsumer(ctx.createQueue(responseDestinationName));
        	System.out.println(consumer.receiveBody(String.class, 1000L));
        }
    }
}
