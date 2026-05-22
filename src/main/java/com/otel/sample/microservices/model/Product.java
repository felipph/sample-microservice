package com.otel.sample.microservices.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Produto a ser cadastrado e processado via fila.
 * Imutável — Jackson deserializa via @JsonCreator.
 */
public class Product {

    private final String productId;
    private final String nome;
    private final BigDecimal preco;
    private final String status;

    @JsonCreator
    public Product(
            @JsonProperty("productId") String productId,
            @JsonProperty("nome") String nome,
            @JsonProperty("preco") BigDecimal preco,
            @JsonProperty("status") String status) {
        this.productId = productId;
        this.nome = nome;
        this.preco = preco;
        this.status = status;
    }

    public String getProductId() { return productId; }
    public String getNome() { return nome; }
    public BigDecimal getPreco() { return preco; }
    public String getStatus() { return status; }
}
