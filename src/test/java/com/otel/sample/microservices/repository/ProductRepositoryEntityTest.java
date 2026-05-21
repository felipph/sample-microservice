package com.otel.sample.microservices.repository;

import com.otel.sample.microservices.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductRepository - Product entity")
class ProductRepositoryEntityTest {

    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ProductRepository();
    }

    @Test
    @DisplayName("deve salvar e encontrar produto por id")
    void shouldSaveAndFindById() {
        // Arrange
        Product product = new Product();
        product.setName("Monitor");
        product.setPrice(new BigDecimal("1999.00"));

        // Act
        Product saved = repository.save(product);
        Optional<Product> found = repository.findProductById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Monitor");
        assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("1999.00"));
    }

    @Test
    @DisplayName("deve retornar empty quando produto nao existe")
    void shouldReturnEmpty_whenProductNotFound() {
        // Act
        Optional<Product> found = repository.findProductById("nonexistent");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("deve atualizar produto existente ao salvar com mesmo id")
    void shouldUpdate_whenSavingExistingProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Mouse");
        product.setPrice(new BigDecimal("49.99"));
        repository.save(product);

        // Act
        product.setPrice(new BigDecimal("59.99"));
        repository.save(product);

        Optional<Product> found = repository.findProductById(product.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("59.99"));
    }
}
