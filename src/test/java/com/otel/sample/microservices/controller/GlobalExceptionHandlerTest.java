package com.otel.sample.microservices.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa os handlers do GlobalExceptionHandler diretamente (unit).
 * O handler de MethodArgumentNotValidException é coberto via ProductControllerTest (@WebMvcTest).
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve retornar 400 com mensagem quando IllegalArgumentException é lançada")
    void should_return_400_with_message_when_illegal_argument() {
        var response = handler.handleIllegalArgumentException(
                new IllegalArgumentException("produto inválido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("message", "produto inválido")
                .containsKey("timestamp")
                .containsKey("status");
    }

    @Test
    @DisplayName("Deve retornar 409 com mensagem quando IllegalStateException é lançada")
    void should_return_409_with_message_when_illegal_state() {
        var response = handler.handleIllegalStateException(
                new IllegalStateException("estado inválido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("message", "estado inválido");
    }

    @Test
    @DisplayName("Deve retornar 500 com mensagem genérica quando exceção inesperada é lançada")
    void should_return_500_with_generic_message_when_unexpected_exception() {
        var response = handler.handleGenericException(new RuntimeException("erro interno"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsKey("error");
    }
}
