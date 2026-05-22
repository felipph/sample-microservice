package com.otel.sample.microservices.messaging;

import com.otel.sample.microservices.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Consome produtos de products-input e retransmite sem transformação para products-output.
 */
@Component
public class ProductMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductMessageConsumer.class);

    private final RabbitTemplate rabbitTemplate;
    private final Queue productsOutputQueue;

    public ProductMessageConsumer(RabbitTemplate rabbitTemplate,
                                   @Qualifier("productsOutputQueue") Queue productsOutputQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.productsOutputQueue = productsOutputQueue;
    }

    @RabbitListener(queues = "${app.queue.products-input:products-input}")
    public void receive(Product product) {
        log.info("Recebido de products-input: {} — retransmitindo para {}",
                product.getProductId(), productsOutputQueue.getName());
        rabbitTemplate.convertAndSend(productsOutputQueue.getName(), product);
        log.debug("Produto retransmitido para {}: {}", productsOutputQueue.getName(), product.getProductId());
    }
}
