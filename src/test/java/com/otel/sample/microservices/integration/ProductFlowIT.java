package com.otel.sample.microservices.integration;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.dto.ProductResponse;
import com.otel.sample.microservices.messaging.ProductMessageConsumer;
import com.otel.sample.microservices.model.Product;
import com.otel.sample.microservices.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "spring.rabbitmq.listener.direct.auto-startup=false"
})
class ProductFlowIT {

    @MockBean
    RabbitTemplate rabbitTemplate;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductMessageConsumer productMessageConsumer;

    @Test
    @DisplayName("Fluxo completo: POST cria produto, retorna 202, consumer retransmite para products-output")
    void should_complete_product_flow() {
        // Arrange
        var request = new ProductRequest("Smart TV 55\"", new BigDecimal("3199.99"));

        // Act 1 — POST /api/products
        var response = restTemplate.postForEntity("/api/products", request, ProductResponse.class);

        // Assert 1 — HTTP 202 Accepted
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo("PENDING");
        assertThat(body.correlationId()).isEqualTo(body.productId());
        assertThat(body.productId()).isNotBlank();

        // Assert 2 — produto foi publicado em products-input
        var productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(rabbitTemplate).convertAndSend(eq("products-input"), productCaptor.capture());

        var sentProduct = productCaptor.getValue();
        assertThat(sentProduct.getNome()).isEqualTo("Smart TV 55\"");
        assertThat(sentProduct.getPreco()).isEqualByComparingTo(new BigDecimal("3199.99"));
        assertThat(sentProduct.getStatus()).isEqualTo("PENDING");
        assertThat(sentProduct.getProductId()).isEqualTo(body.productId());

        // Act 2 — consumer retransmite (simula recebimento da fila products-input)
        productMessageConsumer.receive(sentProduct);

        // Assert 3 — consumer retransmitiu para products-output sem transformação
        verify(rabbitTemplate).convertAndSend(eq("products-output"), eq(sentProduct));
    }

    @Test
    @DisplayName("Deve retornar 400 para produto com nome em branco no fluxo completo")
    void should_return_400_for_blank_nome_in_full_flow() {
        var response = restTemplate.postForEntity("/api/products",
                new ProductRequest("", new BigDecimal("100.00")),
                Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
