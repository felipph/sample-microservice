package com.otel.sample.microservices.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.queue.name:orders.queue}")
    private String orderQueueName;

    @Value("${app.queue.products-input.name:products-input}")
    private String productsInputQueueName;

    @Value("${app.queue.products-output.name:products-output}")
    private String productsOutputQueueName;

    @Bean
    public Queue orderQueue() {
        return new Queue(orderQueueName, true);
    }

    @Bean
    @Qualifier("productsInputQueue")
    public Queue productsInputQueue() {
        return new Queue(productsInputQueueName, true);
    }

    @Bean
    @Qualifier("productsOutputQueue")
    public Queue productsOutputQueue() {
        return new Queue(productsOutputQueueName, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
