package hoge.exp.jms.sharedsubscription;

import javax.jms.Destination;
import javax.jms.JMSContext;

public class MultipleMessagePublisher {
    public static void main(String[] args) {
    	String topicString = "topic01";
    	String message = "hello";
    	int numOfMessages = 10;

    	com.sun.messaging.ConnectionFactory cf =
    			new com.sun.messaging.ConnectionFactory();
    	try (JMSContext ctx = cf.createContext()) {
    		Destination dst = ctx.createTopic(topicString);
    		for (int i = 0; i < numOfMessages; i++) {
    			ctx.createProducer().send(dst, message + " " + i);
    		}
    	}
    }
}
