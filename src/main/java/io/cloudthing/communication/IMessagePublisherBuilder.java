package io.cloudthing.communication;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by kleptoman on 22.12.16.
 */
public interface IMessagePublisherBuilder {

    IMessagePublisher build() throws MqttException;

    IMessagePublisherBuilder setMessageType(IMessagePublisher.MessageType messageType) throws Exception;
}
