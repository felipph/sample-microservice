package com.otel.sample.microservices.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Request para cadastro de produto.
 */
public record ProductRequest(
        @NotBlank String nome,
        @Positive BigDecimal preco
) {}
