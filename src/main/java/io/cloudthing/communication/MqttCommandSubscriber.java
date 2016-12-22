package io.cloudthing.communication;

import io.cloudthing.sdk.device.connectivity.mqtt.ClientWrapper;
import io.cloudthing.sdk.device.utils.CredentialCache;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by kleptoman on 22.12.16.
 */
public class MqttCommandSubscriber {

    private static final String COMMAND_TOPIC_TMPL = "v1/%s/commands/+";

    private ClientWrapper clientWrapper;

    public MqttCommandSubscriber() {
        CredentialCache credentials = CredentialCache.getInstance();
        clientWrapper = new ClientWrapper(credentials.getTenant(), credentials.getDeviceId(), credentials.getToken());
        clientWrapper.setCallback(
                new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        System.out.println("Connection to server lost!");
                        System.exit(1);
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println("Command arrived on topic: " + topic);
                        System.out.println(new String(message.getPayload(), "UTF-8"));

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("Message delivered");
                        System.exit(0);
                    }
                }
        );
    }

    public void subscribe() throws MqttException {
        connectToServer();
        clientWrapper.subscribe(getTopic());
    }

    private void connectToServer() throws MqttException {
        clientWrapper.connect();
    }

    public void disconnectFromServer() throws MqttException {
        clientWrapper.disconnect();
    }

    private String getTopic() {
        return String.format(COMMAND_TOPIC_TMPL, CredentialCache.getInstance().getDeviceId());
    }
}
