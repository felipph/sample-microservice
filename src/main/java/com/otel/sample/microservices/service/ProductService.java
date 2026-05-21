package com.otel.sample.microservices.service;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.dto.ProductResponse;

/**
 * Contrato para cadastro de produtos via API REST com publicação em fila RabbitMQ.
 */
public interface ProductService {

    /**
     * Cria um produto, persiste no repositório e publica na fila products-input.
     *
     * @param request dados do produto (nome, preco)
     * @return resposta com correlationId == productId (mesmo UUID) e status PENDING
     */
    ProductResponse createProduct(ProductRequest request);
}
