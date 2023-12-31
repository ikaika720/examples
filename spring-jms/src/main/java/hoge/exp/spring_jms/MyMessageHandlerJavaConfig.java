package hoge.exp.spring_jms;

import jakarta.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

@Configuration
public class MyMessageHandlerJavaConfig {
	private String brokerURL = "tcp://localhost:61616";
	private String destinationName = "queue01";

    public void handleMessage(String text) {
    	System.out.println(text);
    }

    public static void main(String[] args) {
    	AnnotationConfigApplicationContext ctx =
    			new AnnotationConfigApplicationContext(MyMessageHandlerJavaConfig.class);
    	ctx.registerShutdownHook();
    }

    @Bean
    ConnectionFactory connectionFactory() {
    	return new ActiveMQConnectionFactory(brokerURL);
    }

    @Bean
    MessageListenerAdapter messageListener() {
    	return new MessageListenerAdapter(new MyMessageHandlerJavaConfig());
    }

    @Bean
    SimpleMessageListenerContainer messageListenerContainer(final ConnectionFactory cf,
    		final MessageListenerAdapter messageLisner) {
    	return new SimpleMessageListenerContainer() {
    		// instance initialization block
    		{
    			setConnectionFactory(cf);
    			setMessageListener(messageLisner);
    			setDestinationName(destinationName);
    		}
    	};
    }
}
