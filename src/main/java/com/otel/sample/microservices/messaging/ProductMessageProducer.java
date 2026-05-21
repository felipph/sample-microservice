package com.otel.sample.microservices.messaging;

import com.otel.sample.microservices.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProductMessageProducer implements ProductMessageSender {

    private static final Logger log = LoggerFactory.getLogger(ProductMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.queue.products-input:products-input}")
    private String productsInputQueue;

    public ProductMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(Product product) {
        log.info("Sending product to queue: {} - Queue: {}", product.getId(), productsInputQueue);
        rabbitTemplate.convertAndSend(productsInputQueue, product);
        log.debug("Product sent successfully: {}", product.getId());
    }
}
