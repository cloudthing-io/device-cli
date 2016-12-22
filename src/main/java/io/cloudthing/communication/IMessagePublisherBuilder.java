package io.cloudthing.communication;

/**
 * Created by kleptoman on 22.12.16.
 */
public interface IMessagePublisherBuilder {

    IMessagePublisher build();

    IMessagePublisherBuilder setMessageType(IMessagePublisher.MessageType messageType) throws Exception;
}
