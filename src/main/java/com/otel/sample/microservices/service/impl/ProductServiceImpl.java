package com.otel.sample.microservices.service.impl;

import com.otel.sample.microservices.dto.ProductRequest;
import com.otel.sample.microservices.dto.ProductResponse;
import com.otel.sample.microservices.messaging.ProductMessageProducer;
import com.otel.sample.microservices.model.Product;
import com.otel.sample.microservices.repository.ProductRepository;
import com.otel.sample.microservices.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMessageProducer productMessageProducer;

    public ProductServiceImpl(ProductRepository productRepository,
                               ProductMessageProducer productMessageProducer) {
        this.productRepository = productRepository;
        this.productMessageProducer = productMessageProducer;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        var productId = UUID.randomUUID().toString();
        log.info("Criando produto id={} nome={}", productId, request.nome());

        var product = new Product(productId, request.nome(), request.preco());
        productRepository.save(product);
        log.info("Produto salvo no repositório: {}", productId);

        productMessageProducer.sendProduct(product);
        log.info("Produto publicado em products-input: {}", productId);

        // correlationId == productId conforme especificação
        return new ProductResponse(productId, productId, "PENDING");
    }
}
