package com.otel.sample.microservices.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO de entrada para cadastro de produto.
 * Sem Lombok — record nativo Java 21.
 */
public record ProductRequest(
        @NotBlank String nome,
        @Positive BigDecimal preco) {
}
