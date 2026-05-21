package com.otel.sample.microservices.integration;

import com.otel.sample.microservices.config.TestRabbitConfig;
import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.dto.ProductResponse;
import com.otel.sample.microservices.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestRabbitConfig.class)
class ProductFlowIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Fluxo completo: POST /api/products → 202 com correlationId==productId e status PENDING")
    void should_return_202_with_correlationId_equal_productId_and_PENDING_status() {
        var request = new ProductRequest("Notebook IT", new BigDecimal("2000.00"));

        ResponseEntity<ProductResponse> response =
                restTemplate.postForEntity("/api/products", request, ProductResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        var body = response.getBody();
        assertThat(body.correlationId()).isEqualTo(body.productId());
        assertThat(body.status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Fluxo completo: produto cadastrado deve ser salvo no repositório")
    void should_save_product_in_repository_when_created() {
        var request = new ProductRequest("Monitor IT", new BigDecimal("1800.00"));

        ResponseEntity<ProductResponse> response =
                restTemplate.postForEntity("/api/products", request, ProductResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        var productId = response.getBody().productId();
        var saved = productRepository.findRegisteredById(productId);
        assertThat(saved).isPresent();
        assertThat(saved.get().getNome()).isEqualTo("Monitor IT");
    }

    @Test
    @DisplayName("Fluxo completo: ProductMessageConsumer retransmite de products-input para products-output")
    void should_retransmit_product_from_products_input_to_products_output() {
        var request = new ProductRequest("Teclado IT", new BigDecimal("300.00"));

        restTemplate.postForEntity("/api/products", request, ProductResponse.class);

        // Aguarda o consumer processar e reencaminhar para products-output
        await().atMost(10, SECONDS).untilAsserted(() -> {
            var message = rabbitTemplate.receive("products-output", 500);
            assertThat(message).isNotNull();
        });
    }

    @Test
    @DisplayName("Fluxo completo: POST com nome em branco retorna 400 com lista de campos inválidos")
    void should_return_400_with_campos_when_nome_is_blank() {
        var request = new ProductRequest("", new BigDecimal("100.00"));

        ResponseEntity<java.util.Map> response =
                restTemplate.postForEntity("/api/products", request, java.util.Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("campos");
    }
}
