package hoge.exp.jms.sharedsubscription;

import java.io.IOException;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class Subscriber {
    public static void main(String[] args) {
    	String topicString = "topic01";
    	String sharedSubscriptionName = "subscription01";

    	com.sun.messaging.ConnectionFactory cf =
    			new com.sun.messaging.ConnectionFactory();
    	try (JMSContext ctx = cf.createContext()) {
    		Topic topic = ctx.createTopic(topicString);

    		JMSConsumer consumer =
    				ctx.createSharedConsumer(topic, sharedSubscriptionName);

    		consumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					try {
						System.out.println(((TextMessage) message).getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});

    		System.out.println("Press any key to exit.");
    		try {System.in.read();} catch (IOException e) {}
    	}
    }
}
