package hoge.exp.spring_jms;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MyMessageHandler {
    public void handleMessage(String text) {
    	System.out.println(text);
    }

    public static void main(String[] args) throws InterruptedException {
    	try (ClassPathXmlApplicationContext ctx =
    			new ClassPathXmlApplicationContext("my-message-handler.xml")) {
    	    ctx.registerShutdownHook();
    	    
    	    System.out.println("Message handler started. Press Ctrl+C to stop.");
    	    new CountDownLatch(1).await();
        }
    }
}
