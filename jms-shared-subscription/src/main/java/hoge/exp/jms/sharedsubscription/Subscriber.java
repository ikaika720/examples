package hoge.exp.jms.sharedsubscription;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static java.util.stream.IntStream.range;

import java.util.concurrent.CountDownLatch;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;

public class Subscriber {
	public static void main(String[] args) {
		String brokerURL = "tcp://localhost:61616";
		String username = "artemis";
		String password = "artemis";
		String topicString = "topic01";
		String sharedSubscriptionName = "subscription01";
		int numThreads = 2;

		try (var cf = new ActiveMQConnectionFactory(brokerURL, username, password);
				var executor = newVirtualThreadPerTaskExecutor()) {
			var latch = new CountDownLatch(1);

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("Shutting down...");
				latch.countDown();
			}));

			range(0, numThreads).forEach(i -> {
				int threadId = i;
				executor.execute(() -> {
					System.out.println("Thead " + threadId + " started.");
					try (var ctx = cf.createContext()) {
						var topic = ctx.createTopic(topicString);
						var consumer = ctx.createSharedConsumer(topic, sharedSubscriptionName);

						consumer.setMessageListener(message -> {
							try {
								String text = ((TextMessage) message).getText();
								runAsync(() -> {
									System.out.println("Thread-" + threadId + ": " + text);
								}, executor);
							} catch (JMSException e) {
								e.printStackTrace();
							}
						});

						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
			});

			System.out.println("Started " + numThreads + " subscriber threads. Press Ctrl+C to stop.");
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
