package hoge.exp.jms.sharedsubscription;

import javax.jms.Destination;
import javax.jms.JMSContext;

public class Publisher {
    public static void main(String[] args) {
    	String topicString = "topic01";
    	String message = "hello";

    	com.sun.messaging.ConnectionFactory cf = new com.sun.messaging.ConnectionFactory();
    	try (JMSContext ctx = cf.createContext()) {
    		Destination dst = ctx.createTopic(topicString);
    		ctx.createProducer().send(dst, message);
    	}
    }
}
