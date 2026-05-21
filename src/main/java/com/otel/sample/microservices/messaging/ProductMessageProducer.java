package com.otel.sample.microservices.messaging;

import com.otel.sample.microservices.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Publica mensagens de produto na fila products-input.
 */
@Component
public class ProductMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(ProductMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final Queue productsInputQueue;

    public ProductMessageProducer(RabbitTemplate rabbitTemplate,
                                   @Qualifier("productsInputQueue") Queue productsInputQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.productsInputQueue = productsInputQueue;
    }

    public void sendProduct(Product product) {
        log.info("Publicando produto em {}: id={}", productsInputQueue.getName(), product.getProductId());
        rabbitTemplate.convertAndSend(productsInputQueue.getName(), product);
        log.debug("Produto publicado com sucesso: {}", product.getProductId());
    }
}
