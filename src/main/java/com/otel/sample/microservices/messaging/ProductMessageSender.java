package com.otel.sample.microservices.messaging;

import com.otel.sample.microservices.model.Product;

public interface ProductMessageSender {

    void send(Product product);
}
