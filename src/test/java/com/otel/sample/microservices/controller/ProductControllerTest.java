package com.otel.sample.microservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.dto.ProductResponse;
import com.otel.sample.microservices.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProductService productService;

    @Test
    @DisplayName("Deve retornar 202 Accepted com body correto ao criar produto válido")
    void should_return_202_when_creating_valid_product() throws Exception {
        var uuid = UUID.randomUUID().toString();
        when(productService.createProduct(any())).thenReturn(new ProductResponse(uuid, uuid, "PENDING"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Notebook", "preco": 2999.90}
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.correlationId").value(uuid))
                .andExpect(jsonPath("$.productId").value(uuid))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("correlationId deve ser igual ao productId no retorno")
    void should_have_correlationId_equal_to_productId() throws Exception {
        var uuid = UUID.randomUUID().toString();
        when(productService.createProduct(any())).thenReturn(new ProductResponse(uuid, uuid, "PENDING"));

        var result = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Mouse", "preco": 59.90}
                                """))
                .andExpect(status().isAccepted())
                .andReturn();

        var body = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponse.class);
        assertThat(body.correlationId()).isEqualTo(body.productId());
    }

    @Test
    @DisplayName("Deve retornar 400 quando nome está em branco")
    void should_return_400_when_nome_is_blank() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "", "preco": 99.99}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields[0]").value("nome"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando nome é nulo")
    void should_return_400_when_nome_is_null() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"preco": 99.99}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").isArray());
    }

    @Test
    @DisplayName("Deve retornar 400 quando preço é negativo")
    void should_return_400_when_preco_is_negative() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Produto", "preco": -10.00}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields[0]").value("preco"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando preço é zero")
    void should_return_400_when_preco_is_zero() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Produto", "preco": 0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").isArray());
    }

    @Test
    @DisplayName("Deve aceitar produto com dados válidos mesmo com preço decimal")
    void should_accept_product_with_decimal_preco() throws Exception {
        var uuid = UUID.randomUUID().toString();
        when(productService.createProduct(any())).thenReturn(new ProductResponse(uuid, uuid, "PENDING"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProductRequest("Teclado Mecânico", new BigDecimal("349.90")))))
                .andExpect(status().isAccepted());
    }
}
