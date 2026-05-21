package com.otel.sample.microservices.config;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuração de teste que substitui o ConnectionFactory real pelo mock embarcado,
 * permitindo testes de integração sem um RabbitMQ externo.
 */
@TestConfiguration
public class TestRabbitConfig {

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(new MockConnectionFactory());
    }
}
