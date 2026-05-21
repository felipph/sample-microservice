package com.otel.sample.microservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.dto.ProductResponse;
import com.otel.sample.microservices.model.Product;
import com.otel.sample.microservices.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("deve retornar 202 Accepted com correlationId, productId e status PENDING quando produto valido")
    void shouldReturn202Accepted_whenValidProduct() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest("Notebook", "Notebook gamer", new BigDecimal("4999.99"));

        Product savedProduct = new Product();
        savedProduct.setName("Notebook");
        savedProduct.setDescription("Notebook gamer");
        savedProduct.setPrice(new BigDecimal("4999.99"));

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(savedProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.correlationId").isNotEmpty())
                .andExpect(jsonPath("$.productId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("deve retornar 400 Bad Request com error e field quando name esta em branco")
    void shouldReturn400WithErrorAndField_whenNameIsBlank() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest("", "Description", new BigDecimal("99.99"));

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.field").value("name"));
    }

    @Test
    @DisplayName("deve retornar 400 Bad Request com error e field quando price eh null")
    void shouldReturn400WithErrorAndField_whenPriceIsNull() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest("Mouse", "Mouse gamer", null);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.field").value("price"));
    }

    @Test
    @DisplayName("deve retornar 400 Bad Request com error e field quando price eh zero")
    void shouldReturn400WithErrorAndField_whenPriceIsZero() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest("Mouse", "Mouse gamer", BigDecimal.ZERO);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.field").value("price"));
    }

    @Test
    @DisplayName("deve retornar 400 Bad Request com error e field quando name eh null")
    void shouldReturn400WithErrorAndField_whenNameIsNull() throws Exception {
        // Arrange
        String json = "{\"description\":\"Some desc\",\"price\":10.00}";

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.field").value("name"));
    }
}
