package hoge.exp.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SampleMqttAuthPublisher {
	public static void main(String[] args) {
		String uri = "tcp://localhost:1883";
		String clientId = "publisher01";
		String topic = "topic01";
		String message = "hello";
		String userName = "user01";
		String password = "password";

		MqttClient client = null;
		try {
				client = new MqttClient(uri, clientId, new MemoryPersistence());

				MqttConnectOptions options = new MqttConnectOptions();
				options.setUserName(userName);
				options.setPassword(password.toCharArray());

				client.connect(options);

				client.publish(topic, new MqttMessage(message.getBytes()));
			} catch (MqttException e) {
				e.printStackTrace();
			} finally {
				if (client != null) {
					try {
						if (client.isConnected()) {
							client.disconnect();
						}
						client.close();
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}
			}
	}
}
