package com.otel.sample.microservices.service.impl;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.messaging.ProductMessageSender;
import com.otel.sample.microservices.model.Product;
import com.otel.sample.microservices.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl")
class ProductServiceImplTest {

    @Mock
    private ProductMessageSender messageSender;

    private final ProductRepository productRepository = new ProductRepository();

    private ProductServiceImpl productService;

    @Test
    @DisplayName("deve salvar produto no repositorio e publicar na fila products-input")
    void shouldSaveProductAndPublishToQueue_whenCreateProduct() {
        // Arrange
        productService = new ProductServiceImpl(productRepository, messageSender);
        ProductRequest request = new ProductRequest("Teclado", "Teclado mecanico", new BigDecimal("299.99"));

        // Act
        Product result = productService.createProduct(request);

        // Assert - saved in repository
        Product found = productRepository.findProductById(result.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Teclado");
        assertThat(found.getDescription()).isEqualTo("Teclado mecanico");
        assertThat(found.getPrice()).isEqualByComparingTo(new BigDecimal("299.99"));
        assertThat(found.getStatus()).isEqualTo("PENDING");
        assertThat(found.getId()).isNotNull();
        assertThat(found.getCorrelationId()).isNotNull();

        // Assert - published to queue
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(messageSender, times(1)).send(productCaptor.capture());

        Product sentProduct = productCaptor.getValue();
        assertThat(sentProduct.getId()).isEqualTo(result.getId());
        assertThat(sentProduct.getCorrelationId()).isEqualTo(result.getCorrelationId());

        // Assert - returned product matches saved
        assertThat(result.getName()).isEqualTo("Teclado");
    }

    @Test
    @DisplayName("deve gerar UUID no construtor do produto")
    void shouldGenerateUUIDInConstructor_whenProductCreated() {
        // Arrange
        productService = new ProductServiceImpl(productRepository, messageSender);
        ProductRequest request = new ProductRequest("Monitor", null, new BigDecimal("1999.00"));

        // Act
        Product result = productService.createProduct(request);

        // Assert
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getCorrelationId()).isNotBlank();
        assertThat(result.getId()).isNotEqualTo(result.getCorrelationId());
    }

    @Test
    @DisplayName("deve setar status PENDING no novo produto")
    void shouldSetStatusPending_whenProductCreated() {
        // Arrange
        productService = new ProductServiceImpl(productRepository, messageSender);
        ProductRequest request = new ProductRequest("Headset", "Headset USB", new BigDecimal("449.90"));

        // Act
        Product result = productService.createProduct(request);

        // Assert
        assertThat(result.getStatus()).isEqualTo("PENDING");
    }
}
