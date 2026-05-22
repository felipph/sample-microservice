package com.otel.sample.microservices.messaging;

import com.otel.sample.microservices.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Produces order messages to RabbitMQ queue.
 */
@Component
public class OrderMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public OrderMessageProducer(RabbitTemplate rabbitTemplate,
                                @Value("${app.queue.name:orders.queue}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    public void sendOrder(Order order) {
        log.info("Sending order to queue: {} - Queue: {}", order.getOrderId(), queueName);
        rabbitTemplate.convertAndSend(queueName, order);
        log.debug("Order sent successfully: {}", order.getOrderId());
    }
}
