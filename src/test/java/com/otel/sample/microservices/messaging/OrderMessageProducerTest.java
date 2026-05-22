package com.otel.sample.microservices.messaging;

import com.otel.sample.microservices.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderMessageProducerTest {

    @Mock
    RabbitTemplate rabbitTemplate;

    OrderMessageProducer orderMessageProducer;

    @BeforeEach
    void setUp() {
        orderMessageProducer = new OrderMessageProducer(rabbitTemplate, "orders.queue");
    }

    @Test
    @DisplayName("Deve enviar order para a fila configurada via @Value")
    void should_send_order_to_configured_queue() {
        var order = new Order();
        order.setOrderId("ORD-001");

        orderMessageProducer.sendOrder(order);

        verify(rabbitTemplate).convertAndSend("orders.queue", order);
    }
}
