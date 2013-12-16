package hoge.exp.spring_jms;

import javax.jms.ConnectionFactory;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ResourceAdapter;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ra.ActiveMQActivationSpec;
import org.apache.activemq.ra.ActiveMQResourceAdapter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jca.support.ResourceAdapterFactoryBean;
import org.springframework.jca.work.SimpleTaskWorkManager;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;

@Configuration
public class MyMessageHandlerJCA {
    private String brokerURL = "tcp://localhost:61616";
    private String destinationName = "queue01";

    public void handleMessage(String text) {
        System.out.println(text);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(MyMessageHandlerJCA.class);
        ctx.registerShutdownHook();
    }

    @Bean
    ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(brokerURL);
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new MyMessageHandlerJCA());
    }

    @Bean
    ResourceAdapterFactoryBean resourceAdapterFactoryBean() {
        return new ResourceAdapterFactoryBean() {
            {
                setResourceAdapter(new ActiveMQResourceAdapter() {
                    {
                        setServerUrl(brokerURL);
                    }
                });
                setWorkManager(new SimpleTaskWorkManager());
            }
        };
    }

    ResourceAdapter resourceAdapter() {
        return resourceAdapterFactoryBean().getObject();
    }

    @Bean
    ActivationSpec activemqActivationSpec() {
        return new ActiveMQActivationSpec() {
            private static final long serialVersionUID = 1L;
            {
                setDestination(destinationName);
                setDestinationType("javax.jms.Queue");
            }
        };
    }

    @Bean
    JmsMessageEndpointManager messageEndpointManager(
            final ActivationSpec activationSpec,
            final ResourceAdapter resourceAdapter,
            final MessageListenerAdapter messageLisner) {
        return new JmsMessageEndpointManager() {
            {
                setActivationSpec(activationSpec);
                setMessageListener(messageLisner);
                setResourceAdapter(resourceAdapter);
            }
        };
    }
}
