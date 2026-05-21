package com.otel.sample.microservices.model;

import java.math.BigDecimal;

/**
 * Modelo de domínio para produtos cadastrados via API.
 */
public class Product {

    private String productId;
    private String nome;
    private BigDecimal preco;

    // Necessário para desserialização Jackson (RabbitMQ JSON messages)
    public Product() {}

    public Product(String productId, String nome, BigDecimal preco) {
        this.productId = productId;
        this.nome = nome;
        this.preco = preco;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    @Override
    public String toString() {
        return "Product{productId='" + productId + "', nome='" + nome + "', preco=" + preco + "}";
    }
}
