package com.otel.sample.microservices.dto;

public class ProductResponse {

    private String correlationId;
    private String productId;
    private String status;

    public ProductResponse() {
    }

    public ProductResponse(String correlationId, String productId, String status) {
        this.correlationId = correlationId;
        this.productId = productId;
        this.status = status;
    }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "correlationId='" + correlationId + '\'' +
                ", productId='" + productId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
