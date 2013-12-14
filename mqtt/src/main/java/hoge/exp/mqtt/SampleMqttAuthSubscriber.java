package hoge.exp.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SampleMqttAuthSubscriber implements MqttCallback {
	public static void main(String[] args) {
		String uri = "tcp://localhost:1883";
		String clientId = "subscriber01";
		String topic = "topic01";
		String userName = "user02";
		String password = "password";

		MqttClient client = null;
		try {
			client = new MqttClient(uri, clientId, new MemoryPersistence());

			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName(userName);
			options.setPassword(password.toCharArray());

			client.connect(options);

			client.setCallback(new SampleMqttAuthSubscriber());
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
