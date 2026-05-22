package com.otel.sample.microservices.service;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.dto.ProductResponse;

public interface ProductService {

    /**
     * Cadastra um produto, persiste e publica na fila products-input.
     * Retorna 202 Accepted com correlationId == productId.
     */
    ProductResponse createProduct(ProductRequest request);
}
