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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Deve retornar 202 Accepted com correlationId igual a productId e status PENDING ao criar produto válido")
    void should_return_202_with_correlationId_equal_to_productId_and_PENDING_when_valid_product()
            throws Exception {
        var uuid = "550e8400-e29b-41d4-a716-446655440000";
        var request = new ProductRequest("Notebook", new BigDecimal("1500.00"));
        var response = new ProductResponse(uuid, uuid, "PENDING");

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.correlationId").value(uuid))
                .andExpect(jsonPath("$.productId").value(uuid))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Deve retornar 400 com lista de campos inválidos quando nome está em branco")
    void should_return_400_with_campos_list_when_nome_is_blank() throws Exception {
        var request = new ProductRequest("", new BigDecimal("100.00"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos").isArray())
                .andExpect(jsonPath("$.campos[0]").value("nome"));
    }

    @Test
    @DisplayName("Deve retornar 400 com lista de campos inválidos quando preço não é positivo")
    void should_return_400_with_campos_list_when_preco_is_not_positive() throws Exception {
        var request = new ProductRequest("Notebook", new BigDecimal("-1.00"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos").isArray())
                .andExpect(jsonPath("$.campos[0]").value("preco"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando nome é nulo")
    void should_return_400_when_nome_is_null() throws Exception {
        var json = """
                {"nome": null, "preco": 100.00}
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos").isArray());
    }
}
