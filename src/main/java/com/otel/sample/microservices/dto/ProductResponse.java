package com.otel.sample.microservices.dto;

/**
 * DTO de resposta para cadastro de produto.
 * correlationId == productId (mesmo UUID) conforme especificação.
 * Sem Lombok — record nativo Java 21.
 */
public record ProductResponse(String correlationId, String productId, String status) {
}
