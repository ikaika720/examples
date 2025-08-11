package hoge.exp.spring_jms;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;

@Configuration
@EnableJms
public class SampleRequestResponseServer {
	private static final String BROKER_URL = "tcp://localhost:61616";
	private static final String REQ_DEST = "request";
	private static final String RES_DEST = "response";
	private static final String USERNAME = "artemis";
	private static final String PASSWORD = "artemis";

	@Autowired
	JmsTemplate jmsTemplate;

    @JmsListener(destination = REQ_DEST)
    public void handleMessage(String text) {
    	System.out.println(text);
    	jmsTemplate.convertAndSend(new Date().toString());
    }

    public static void main(String[] args) throws InterruptedException {
    	try (var ctx = new AnnotationConfigApplicationContext(SampleRequestResponseServer.class)) {
    		ctx.registerShutdownHook();
    		
    		System.out.println("Server started. Press Ctrl+C to stop.");
    		new CountDownLatch(1).await();
    	}
    }

    @Bean
    ConnectionFactory connectionFactory() {
    	return new ActiveMQConnectionFactory(BROKER_URL, USERNAME, PASSWORD);
    }

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory cf) {
		var factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(cf);
		return factory;
	}

	@Bean
    JmsTemplate jmsTemplate(ConnectionFactory cf) {
    	var template = new JmsTemplate(cf);
    	template.setDefaultDestinationName(RES_DEST);
    	return template;
    }
}
