package io.cloudthing.communication;

import io.cloudthing.sdk.device.connectivity.mqtt.IMqttCloudthingClient;
import io.cloudthing.sdk.device.connectivity.mqtt.MqttCloudthingClientBuilder;
import io.cloudthing.sdk.device.data.EventPayload;
import io.cloudthing.sdk.device.data.ICloudThingMessage;
import io.cloudthing.sdk.device.utils.CredentialCache;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by kleptoman on 22.12.16.
 */
public class MqttMessagePublisher implements IMessagePublisher {

    private static final String DATA_TOPIC_TMPL = "v1/%s/data?ct=json";
    private static final String EVENT_TOPIC_TMPL = "v1/%s/events/%s";

    public static MessagePublisherBuilder builder() {
        return new MessagePublisherBuilder();
    }

    private IMqttCloudthingClient mqttCloudthingClient;

    private IMessagePublisher.MessageType messageType;
    private String eventId;
    private String payload;

    MqttMessagePublisher() {

    }

    public IMessagePublisher.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(IMessagePublisher.MessageType messageType) throws Exception {
        this.messageType = messageType;
    }

    @Override
    public void setMessagePayload(String payload) {
       this.payload = payload;
    }

    @Override
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    @Override
    public void sendMessage(ICloudThingMessage message) throws Exception {
        connectToServer();
        mqttCloudthingClient.publish(getTopic(), message,1);
        disconnectFromServer();
    }

    private void connectToServer() throws MqttException {
        mqttCloudthingClient.connect();
    }

    private void disconnectFromServer() throws MqttException {
        mqttCloudthingClient.disconnect();
    }

    private String getTopic() {
        switch (messageType) {
            case DATA:
                return String.format(DATA_TOPIC_TMPL, CredentialCache.getInstance().getDeviceId());

            case EVENT:
                return String.format(EVENT_TOPIC_TMPL, CredentialCache.getInstance().getDeviceId(), getEventId());

        }
        throw new IllegalStateException("Invalid message type!");
    }

    private ICloudThingMessage getMessage() {
        EventPayload payload = new EventPayload();
        payload.setPayload(this.payload);
        return payload;
    }

    /**
     * Created by kleptoman on 22.12.16.
     */
    public static class MessagePublisherBuilder implements IMessagePublisherBuilder {

        private MqttMessagePublisher publisher;

        public MessagePublisherBuilder() {
            publisher = new MqttMessagePublisher();
        }

        @Override
        public MqttMessagePublisher build() throws MqttException {
            CredentialCache credentials = CredentialCache.getInstance();

            publisher.mqttCloudthingClient = MqttCloudthingClientBuilder
                    .getBuilder()
                    .setTenant(credentials.getTenant())
                    .setQos(1)
                    .setDeviceId(credentials.getDeviceId())
                    .setToken(credentials.getToken())
                    .setSecure(true)
                    .setServerTemplate("{tenant}.cloudthing.io")
                    .build();

            publisher.mqttCloudthingClient.setCallback(
                    new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {
                            System.out.println("Connection to server lost!");
                            System.exit(1);
                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {

                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {
                            System.out.println("Message delivered");
                            System.exit(0);
                        }
                    }
            );
            return publisher;
        }

        @Override
        public MessagePublisherBuilder setMessageType(IMessagePublisher.MessageType type) throws Exception {
            publisher.setMessageType(type);
            return this;
        }
    }
}
