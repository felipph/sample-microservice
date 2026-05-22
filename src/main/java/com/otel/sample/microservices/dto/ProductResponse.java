package com.otel.sample.microservices.dto;

/**
 * Response do cadastro de produto — correlationId == productId (mesmo UUID).
 */
public record ProductResponse(
        String correlationId,
        String productId,
        String status
) {}
