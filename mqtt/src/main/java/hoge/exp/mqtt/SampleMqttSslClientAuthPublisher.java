package hoge.exp.mqtt;

import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SampleMqttSslClientAuthPublisher {
	public static void main(String[] args) {
		String uri = "ssl://localhost:8884";
		String clientId = "publisher01";
		String topic = "topic01";
		String message = "hello";
		String keyStore = "src/main/ssl/client.jks";
		String keyStorePassword = "password";
		String trustStore = "src/main/ssl/client.ts";
		String sslProtocol = "TLSv1.2";

		MqttClient client = null;
		try {
			client = new MqttClient(uri, clientId, new MemoryPersistence());

			Properties props = new Properties();
			props.setProperty("com.ibm.ssl.keyStore", keyStore);
			props.setProperty("com.ibm.ssl.keyStorePassword", keyStorePassword);
			props.setProperty("com.ibm.ssl.trustStore", trustStore);
			props.setProperty("com.ibm.ssl.protocol", sslProtocol);

			MqttConnectOptions options = new MqttConnectOptions();
			options.setSSLProperties(props);

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
