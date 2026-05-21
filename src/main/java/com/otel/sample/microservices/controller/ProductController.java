package com.otel.sample.microservices.controller;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.dto.ProductResponse;
import com.otel.sample.microservices.model.Product;
import com.otel.sample.microservices.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("REST request to create product - Name: {} - Price: {}", request.getName(), request.getPrice());

        Product product = productService.createProduct(request);

        ProductResponse response = new ProductResponse(
                product.getCorrelationId(),
                product.getId(),
                product.getStatus()
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
