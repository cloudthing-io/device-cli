package io.cloudthing.communication;

import io.cloudthing.sdk.device.connectivity.http.StringDataRequestFactory;
import io.cloudthing.sdk.device.connectivity.http.DeviceRequestFactory;
import io.cloudthing.sdk.device.connectivity.http.EventRequestFactory;
import io.cloudthing.sdk.device.connectivity.http.HttpRequestQueue;
import io.cloudthing.sdk.device.data.ICloudThingMessage;
import io.cloudthing.sdk.device.utils.CredentialCache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by kleptoman on 22.12.16.
 */
public class HttpMessagePublisher implements IMessagePublisher {

    public static MessagePublisherBuilder builder() {
        return new MessagePublisherBuilder();
    }

    HttpMessagePublisher() {
        this.requestQueue = HttpRequestQueue.getInstance();
    }

    private HttpRequestQueue requestQueue;
    private DeviceRequestFactory requestFactory;
    private IMessagePublisher.MessageType messageType;

    public IMessagePublisher.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(IMessagePublisher.MessageType messageType) throws Exception {
        this.messageType = messageType;
        CredentialCache credentials = CredentialCache.getInstance();
        this.requestFactory = (DeviceRequestFactory) this.messageType.getRequestFactoryClass().getConstructor(String.class, String.class, String.class)
                .newInstance(credentials.getDeviceId(), credentials.getToken(), credentials.getTenant());
    }

    @Override
    public void setMessagePayload(String payload) {
        switch (messageType) {
            case DATA:
                ((StringDataRequestFactory) requestFactory).setPayload(payload);
                break;

            case EVENT:
                ((EventRequestFactory) requestFactory).setPayload(payload);
                break;
        }
    }

    @Override
    public void setEventId(String eventId) {
        if (IMessagePublisher.MessageType.EVENT.equals(messageType)) {
            ((EventRequestFactory) requestFactory).setEventId(eventId);
        }
    }

    @Override
    public void sendMessage(ICloudThingMessage message) {
        requestQueue.addToRequestQueue(requestFactory.getRequest(), requestFactory.getListener());
    }

    /**
     * Created by kleptoman on 22.12.16.
     */
    public static class MessagePublisherBuilder implements IMessagePublisherBuilder {

        private HttpMessagePublisher publisher;

        public MessagePublisherBuilder() {
            publisher = new HttpMessagePublisher();
        }

        @Override
        public HttpMessagePublisher build() {
            return publisher;
        }

        @Override
        public MessagePublisherBuilder setMessageType(MessageType type) throws Exception {
            publisher.setMessageType(type);
            publisher.requestFactory.setListener(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("Failed to deliver!");
                    e.printStackTrace();
                    System.exit(1);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        System.out.println("Message delivered");
                        System.exit(0);
                    }
                    System.out.println("Message delivery was not successful!");
                    System.out.println(response.code() + ": " + response.message());
                    System.exit(1);
                }
            });
            return this;
        }
    }
}
