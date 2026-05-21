# Task Spec: PROD-E2E-1779383871

**Tipo:** feature
**Titulo:** Cadastrar Produtos via API REST com RabbitMQ

## Objetivo
Cadastrar Produtos via API REST com RabbitMQ

Como usuario do sistema, quero cadastrar produtos via POST /api/products para que sejam publicados na fila products-input e retransmitidos pelo consumer para products-output.

## Criterios de Aceitacao
- POST /api/products retorna 202 Accepted com { correlationId, productId, status: PENDING } onde correlationId == productId (mesmo UUID)
- Produto salvo via ProductRepository.save() e publicado na fila products-input
- ProductMessageConsumer le products-input e retransmite mensagem sem transformacao para products-output
- @NotBlank nome e @Positive preco retornam 400 Bad Request com lista de campos invalidos (via MethodArgumentNotValidException handler no GlobalExceptionHandler)
- spring-boot-starter-validation presente no pom.xml substituindo jakarta.validation-api standalone
- OrderMessageProducer refatorado para @Value com nome de fila; novas filas products-input e products-output com @Qualifier
- Testes unitarios ProductControllerTest e ProductServiceImplTest presentes e passando
- Teste de integracao ProductFlowIT com RabbitMQ embarcado validando fluxo completo
- Constructor injection em todos os novos beans; DTOs sem Lombok

## Modulos afetados
_(nenhum identificado)_

## Padroes existentes a seguir
_(nenhum padrao)_

## Estrategia de testes
TDD estrito: para cada AC, escreva teste(s) falhando primeiro, implemente o mínimo para passar, refatore mantendo verdes. Diff coverage mínimo: 90%.

## EXPLICITAMENTE fora de escopo
_(nenhum)_
