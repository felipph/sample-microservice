package com.otel.sample.microservices.messaging;

import com.otel.sample.microservices.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProductMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductMessageConsumer.class);

    private final QueuePublisher queuePublisher;

    @Value("${app.queue.products-output:products-output}")
    private String productsOutputQueue;

    public ProductMessageConsumer(QueuePublisher queuePublisher) {
        this.queuePublisher = queuePublisher;
    }

    @RabbitListener(queues = "${app.queue.products-input:products-input}")
    public void receiveProduct(Product product) {
        log.info("Received product from products-input: {} - CorrelationId: {}",
                product.getId(), product.getCorrelationId());

        queuePublisher.publish(productsOutputQueue, product);

        log.info("Product forwarded to products-output: {}", product.getId());
    }
}
