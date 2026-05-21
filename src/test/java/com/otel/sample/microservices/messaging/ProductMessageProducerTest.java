package com.otel.sample.microservices.messaging;

import com.otel.sample.microservices.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductMessageProducer")
class ProductMessageProducerTest {

    private final List<SentMessage> sentMessages = new ArrayList<>();

    private static class SentMessage {
        final String queue;
        final Object message;

        SentMessage(String queue, Object message) {
            this.queue = queue;
            this.message = message;
        }
    }

    private final RabbitTemplate rabbitTemplate = new RabbitTemplate() {
        @Override
        public void convertAndSend(String routingKey, Object message) {
            sentMessages.add(new SentMessage(routingKey, message));
        }
    };

    @Test
    @DisplayName("deve publicar produto na fila products-input")
    void shouldPublishToProductsInputQueue() {
        // Arrange
        ProductMessageProducer producer = new ProductMessageProducer(rabbitTemplate);
        ReflectionTestUtils.setField(producer, "productsInputQueue", "products-input");

        Product product = new Product();
        product.setName("Teclado");
        product.setPrice(new BigDecimal("199.99"));

        // Act
        producer.send(product);

        // Assert
        assertThat(sentMessages).hasSize(1);
        assertThat(sentMessages.get(0).queue).isEqualTo("products-input");
        assertThat(sentMessages.get(0).message).isEqualTo(product);
    }

    @Test
    @DisplayName("deve enviar produto com dados corretos")
    void shouldSendProductWithCorrectData() {
        // Arrange
        ProductMessageProducer producer = new ProductMessageProducer(rabbitTemplate);
        ReflectionTestUtils.setField(producer, "productsInputQueue", "products-input");

        Product product = new Product();
        product.setName("Monitor 4K");
        product.setDescription("Monitor IPS 27 polegadas");
        product.setPrice(new BigDecimal("2999.00"));

        // Act
        producer.send(product);

        // Assert
        Product sent = (Product) sentMessages.get(0).message;
        assertThat(sent.getName()).isEqualTo("Monitor 4K");
        assertThat(sent.getDescription()).isEqualTo("Monitor IPS 27 polegadas");
        assertThat(sent.getPrice()).isEqualByComparingTo(new BigDecimal("2999.00"));
        assertThat(sent.getCorrelationId()).isNotNull();
    }
}
