package hoge.exp.spring_jms;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MyMessageHandler {
    public void handleMessage(String text) {
    	System.out.println(text);
    }

    public static void main(String[] args) {
    	ClassPathXmlApplicationContext ctx =
    			new ClassPathXmlApplicationContext("my-message-handler.xml");
    	ctx.registerShutdownHook();
    }
}
