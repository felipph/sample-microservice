package com.otel.sample.microservices.messaging;

import com.otel.sample.microservices.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductMessageConsumer")
class ProductMessageConsumerTest {

    @Mock
    private QueuePublisher queuePublisher;

    private ProductMessageConsumer consumer;

    @Test
    @DisplayName("deve consumir de products-input e publicar em products-output")
    void shouldConsumeFromInputAndPublishToOutput() {
        // Arrange
        consumer = new ProductMessageConsumer(queuePublisher);
        ReflectionTestUtils.setField(consumer, "productsOutputQueue", "products-output");

        Product product = new Product();
        product.setName("Webcam");
        product.setPrice(new BigDecimal("199.99"));

        // Act
        consumer.receiveProduct(product);

        // Assert
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(queuePublisher, times(1)).publish(eq("products-output"), captor.capture());

        Product forwarded = captor.getValue();
        assertThat(forwarded).isEqualTo(product);
        assertThat(forwarded.getName()).isEqualTo("Webcam");
        assertThat(forwarded.getPrice()).isEqualByComparingTo(new BigDecimal("199.99"));
    }

    @Test
    @DisplayName("deve preservar correlationId ao encaminhar produto")
    void shouldPreserveCorrelationId_whenForwarding() {
        // Arrange
        consumer = new ProductMessageConsumer(queuePublisher);
        ReflectionTestUtils.setField(consumer, "productsOutputQueue", "products-output");

        Product product = new Product();
        product.setName("SSD");
        product.setPrice(new BigDecimal("349.90"));
        String originalCorrelationId = product.getCorrelationId();

        // Act
        consumer.receiveProduct(product);

        // Assert
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(queuePublisher).publish(eq("products-output"), captor.capture());

        assertThat(captor.getValue().getCorrelationId()).isEqualTo(originalCorrelationId);
    }
}
