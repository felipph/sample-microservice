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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMessageProducer productMessageProducer;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    @DisplayName("Deve salvar produto no repositório e publicar na fila products-input ao criar produto")
    void should_save_product_and_publish_to_queue_when_creating_product() {
        var request = new ProductRequest("Notebook", new BigDecimal("1500.00"));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = productServiceImpl.createProduct(request);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.productId()).isNotNull().isNotBlank();
        verify(productRepository).save(any(Product.class));
        verify(productMessageProducer).sendProduct(any(Product.class));
    }

    @Test
    @DisplayName("Deve retornar correlationId igual ao productId")
    void should_return_correlationId_equal_to_productId() {
        var request = new ProductRequest("Mouse", new BigDecimal("50.00"));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = productServiceImpl.createProduct(request);

        assertThat(response.correlationId()).isEqualTo(response.productId());
    }

    @Test
    @DisplayName("Deve salvar produto com nome e preço corretos do request")
    void should_save_product_with_correct_nome_and_preco_from_request() {
        var nome = "Monitor 4K";
        var preco = new BigDecimal("2500.00");
        var request = new ProductRequest(nome, preco);
        var captor = ArgumentCaptor.forClass(Product.class);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        productServiceImpl.createProduct(request);

        verify(productRepository).save(captor.capture());
        var savedProduct = captor.getValue();
        assertThat(savedProduct.getNome()).isEqualTo(nome);
        assertThat(savedProduct.getPreco()).isEqualByComparingTo(preco);
        assertThat(savedProduct.getProductId()).isNotNull();
    }

    @Test
    @DisplayName("Deve publicar na fila o produto com o mesmo productId da resposta")
    void should_publish_product_with_same_productId_as_response() {
        var request = new ProductRequest("Teclado", new BigDecimal("200.00"));
        var captor = ArgumentCaptor.forClass(Product.class);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = productServiceImpl.createProduct(request);

        verify(productMessageProducer).sendProduct(captor.capture());
        assertThat(captor.getValue().getProductId()).isEqualTo(response.productId());
    }
}
