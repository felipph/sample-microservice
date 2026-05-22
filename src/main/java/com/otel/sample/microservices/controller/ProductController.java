package com.otel.sample.microservices.controller;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.dto.ProductResponse;
import com.otel.sample.microservices.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        log.info("POST /api/products — nome={}, preco={}", request.nome(), request.preco());
        var response = productService.createProduct(request);
        return ResponseEntity.accepted().body(response);
    }
}
