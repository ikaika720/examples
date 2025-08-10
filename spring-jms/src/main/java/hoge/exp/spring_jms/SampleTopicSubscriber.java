package hoge.exp.spring_jms;

import java.util.concurrent.CountDownLatch;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import jakarta.jms.ConnectionFactory;

@Configuration
public class SampleTopicSubscriber {
	private String brokerURL = "tcp://localhost:61616";
	private String username = "artemis";
	private String password = "artemis";
	private String destinationName = "topic01";

    public void handleMessage(String text) {
    	System.out.println(text);
    }

    public static void main(String[] args) throws InterruptedException {
    	try (AnnotationConfigApplicationContext ctx =
    			new AnnotationConfigApplicationContext(SampleTopicSubscriber.class)) {
    		ctx.registerShutdownHook();
    		
    		System.out.println("Subscriber started. Press Ctrl+C to stop.");
    		new CountDownLatch(1).await();
    	}
    }

    @Bean
    ConnectionFactory connectionFactory() {
    	return new ActiveMQConnectionFactory(brokerURL, username, password);
    }

    @Bean
    MessageListenerAdapter messageListener() {
    	return new MessageListenerAdapter(new SampleTopicSubscriber());
    }

    @Bean
    SimpleMessageListenerContainer messageListenerContainer(final ConnectionFactory cf,
    		final MessageListenerAdapter messageLisner) {
    	return new SimpleMessageListenerContainer() {
    		{
    			setConnectionFactory(cf);
    			setMessageListener(messageLisner);
    			setDestinationName(destinationName);
    			setPubSubDomain(true);
    		}
    	};
    }
}
