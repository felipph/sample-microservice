package com.otel.sample.microservices.service.impl;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.messaging.ProductMessageSender;
import com.otel.sample.microservices.model.Product;
import com.otel.sample.microservices.repository.ProductRepository;
import com.otel.sample.microservices.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMessageSender messageSender;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductMessageSender messageSender) {
        this.productRepository = productRepository;
        this.messageSender = messageSender;
    }

    @Override
    public Product createProduct(ProductRequest request) {
        log.info("Creating product - Name: {} - Price: {}", request.getName(), request.getPrice());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Product savedProduct = productRepository.save(product);
        log.info("Product saved: {} - CorrelationId: {}", savedProduct.getId(), savedProduct.getCorrelationId());

        messageSender.send(savedProduct);
        log.info("Product sent to queue: {}", savedProduct.getId());

        return savedProduct;
    }
}
