package hoge.exp.spring_jms;

import java.util.Date;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

@Configuration
public class SampleRequestResponseServer {
	private String brokerURL = "tcp://localhost:61616";
	private String requestDestinationName = "request";
	private String responseDestinatnionName = "response";

	@Autowired
	JmsTemplate jmsTemplate;

    public void handleMessage(String text) {
    	System.out.println(text);
    	jmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(new Date().toString());
			}
		});
    }

    public static void main(String[] args) {
    	AnnotationConfigApplicationContext ctx =
    			new AnnotationConfigApplicationContext(SampleRequestResponseServer.class);
    	ctx.registerShutdownHook();
    }

    @Bean
    ConnectionFactory connectionFactory() {
    	return new ActiveMQConnectionFactory(brokerURL);
    }

    @Bean
    MessageListenerAdapter messageListener(final SampleRequestResponseServer server) {
    	return new MessageListenerAdapter(server);
    }

    @Bean
    SimpleMessageListenerContainer messageListenerContainer(final ConnectionFactory cf,
    		final MessageListenerAdapter messageLisner) {
    	return new SimpleMessageListenerContainer() {
    		{
    			setConnectionFactory(cf);
    			setMessageListener(messageLisner);
    			setDestinationName(requestDestinationName);
    		}
    	};
    }

    @Bean
    JmsTemplate jmsTemplate(final ConnectionFactory cf) {
    	return new JmsTemplate() {
    		{
    			setConnectionFactory(cf);
    			setDefaultDestinationName(responseDestinatnionName);
    		}
    	};
    }
}
