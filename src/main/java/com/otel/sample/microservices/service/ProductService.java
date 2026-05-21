package com.otel.sample.microservices.service;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.model.Product;

public interface ProductService {

    Product createProduct(ProductRequest request);
}
