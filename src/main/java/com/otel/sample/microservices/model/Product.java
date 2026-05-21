package com.otel.sample.microservices.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Product {

    private String id;
    private String correlationId;
    private String name;
    private String description;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;

    public Product() {
        this.id = UUID.randomUUID().toString();
        this.correlationId = UUID.randomUUID().toString();
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Product(String id, String correlationId, String name,
                   String description, BigDecimal price, String status,
                   LocalDateTime createdAt) {
        this.id = id;
        this.correlationId = correlationId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}
