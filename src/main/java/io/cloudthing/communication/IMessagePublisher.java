package io.cloudthing.communication;

import io.cloudthing.sdk.device.connectivity.http.DeviceRequestFactory;
import io.cloudthing.sdk.device.connectivity.http.EventRequestFactory;
import io.cloudthing.sdk.device.connectivity.http.StringDataRequestFactory;

/**
 * Created by kleptoman on 22.12.16.
 */
public interface IMessagePublisher {

    void setEventId(String eventId);

    void setMessagePayload(String payload);

    void sendMessage() throws Exception;

    enum MessageType {

        DATA(StringDataRequestFactory.class), EVENT(EventRequestFactory.class);

        private Class<? extends DeviceRequestFactory> requestFactoryClass;

        MessageType(Class<? extends DeviceRequestFactory> requestFactoryClass) {
            this.requestFactoryClass = requestFactoryClass;
        }

        public Class<?> getRequestFactoryClass() {
            return requestFactoryClass;
        }
    }
}
