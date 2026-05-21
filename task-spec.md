# Task Spec: PROD-020

**Tipo:** feature
**Titulo:** Cadastrar Produtos via API REST com RabbitMQ

## Objetivo
Cadastrar Produtos via API REST com RabbitMQ

Implementar cadastro de produtos com API REST + processamento assincrono via RabbitMQ.

## Criterios de Aceitacao
- POST /api/products retorna 202 Accepted com {correlationId, productId, status: PENDING}
- Jakota Bean Validation retorna 400 Bad Request com {error, field}
- Produto salvo no ProductRepository e publicado na fila products-input
- ProductMessageConsumer consome products-input, publica em products-output
- Constructor injection, DTOs sem Lombok, UUID no construtor
- Filas: products-input e products-output

## Modulos afetados
_(nenhum identificado)_

## Padroes existentes a seguir
_(nenhum padrao)_

## Estrategia de testes
TDD estrito: para cada AC, escreva teste(s) falhando primeiro, implemente o mínimo para passar, refatore mantendo verdes. Diff coverage mínimo: 90%.

## EXPLICITAMENTE fora de escopo
_(nenhum)_
