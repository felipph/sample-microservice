package com.otel.sample.microservices.service.impl;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.messaging.ProductMessageProducer;
import com.otel.sample.microservices.model.Product;
import com.otel.sample.microservices.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMessageProducer messageProducer;

    @InjectMocks
    ProductServiceImpl productService;

    @Test
    @DisplayName("Deve salvar produto e enviar para fila ao criar produto válido")
    void should_save_product_and_send_to_queue_when_creating_product() {
        var request = new ProductRequest("Notebook Pro", new BigDecimal("3499.00"));

        var response = productService.createProduct(request);

        var captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        verify(messageProducer).sendProduct(captor.getValue());

        var saved = captor.getValue();
        assertThat(saved.getNome()).isEqualTo("Notebook Pro");
        assertThat(saved.getPreco()).isEqualByComparingTo(new BigDecimal("3499.00"));
        assertThat(saved.getStatus()).isEqualTo("PENDING");
        assertThat(saved.getProductId()).isNotBlank();

        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.productId()).isEqualTo(saved.getProductId());
        assertThat(response.correlationId()).isEqualTo(saved.getProductId());
    }

    @Test
    @DisplayName("correlationId deve ser igual ao productId na resposta")
    void should_have_correlationId_equal_to_productId_in_response() {
        var request = new ProductRequest("Monitor 4K", BigDecimal.valueOf(1299.90));

        var response = productService.createProduct(request);

        assertThat(response.correlationId()).isNotBlank();
        assertThat(response.correlationId()).isEqualTo(response.productId());
    }

    @Test
    @DisplayName("Deve gerar productId único para cada criação")
    void should_generate_unique_product_id_for_each_creation() {
        var request = new ProductRequest("Headset", BigDecimal.valueOf(299.00));

        var resp1 = productService.createProduct(request);
        var resp2 = productService.createProduct(request);

        assertThat(resp1.productId()).isNotEqualTo(resp2.productId());
    }

    @Test
    @DisplayName("Deve retornar status PENDING na resposta")
    void should_return_pending_status_in_response() {
        var request = new ProductRequest("Cadeira Gamer", BigDecimal.valueOf(799.00));

        var response = productService.createProduct(request);

        assertThat(response.status()).isEqualTo("PENDING");
    }
}
