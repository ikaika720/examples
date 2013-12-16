package hoge.exp.spring_jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

@Configuration
public class SampleTopicSubscriber {
	private String brokerURL = "tcp://localhost:61616";
	private String destinationName = "topic01";

    public void handleMessage(String text) {
    	System.out.println(text);
    }

    public static void main(String[] args) {
    	AnnotationConfigApplicationContext ctx =
    			new AnnotationConfigApplicationContext(SampleTopicSubscriber.class);
    	ctx.registerShutdownHook();
    }

    @Bean
    ConnectionFactory connectionFactory() {
    	return new ActiveMQConnectionFactory(brokerURL);
    }

    @Bean
    MessageListenerAdapter messageListener() {
    	return new MessageListenerAdapter(new SampleTopicSubscriber());
    }

    @Bean
    SimpleMessageListenerContainer jmsTemplate(final ConnectionFactory cf,
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
