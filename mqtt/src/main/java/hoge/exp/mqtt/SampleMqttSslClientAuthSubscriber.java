package hoge.exp.mqtt;

import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SampleMqttSslClientAuthSubscriber implements MqttCallback {
	public static void main(String[] args) {
		String uri = "ssl://localhost:8883";
		String clientId = "subscriber01";
		String topic = "topic01";
		String keyStore = "src/main/ssl/client.jks";
		String keyStorePassword = "password";
		String trustStore = "src/main/ssl/client.ts";
		String sslProtocol = "TLSv1.2";

		MqttClient client = null;
		try {
			client = new MqttClient(uri, clientId, new MemoryPersistence());

			Properties props = new Properties();
			props.setProperty("com.ibm.ssl.trustStore", trustStore);
			props.setProperty("com.ibm.ssl.protocol", sslProtocol);
			props.setProperty("com.ibm.ssl.keyStore", keyStore);
			props.setProperty("com.ibm.ssl.keyStorePassword", keyStorePassword);

			MqttConnectOptions options = new MqttConnectOptions();
			options.setSSLProperties(props);

			client.connect(options);

			client.setCallback(new SampleMqttSslClientAuthSubscriber());
			client.subscribe(topic);

			System.out.println("Press any key to exit.");
			try {System.in.read();} catch (Exception e) {}
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

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("connectionLost");
		cause.printStackTrace();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {
		System.out.println(new String(message.getPayload()));
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("deliveryComplete");
	}
}
