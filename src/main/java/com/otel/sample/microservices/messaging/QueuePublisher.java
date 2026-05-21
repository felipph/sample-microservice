package com.otel.sample.microservices.messaging;

public interface QueuePublisher {

    void publish(String queue, Object message);
}
