# Task Spec: PROD-E2E-004

**Tipo:** feature
**Titulo:** Cadastrar Produtos via API REST com RabbitMQ

## Objetivo
Cadastrar Produtos via API REST com RabbitMQ

Feature: CRUD de produtos via API REST com processamento assincrono via RabbitMQ.

## Criterios de Aceitacao
- AC1: POST /api/products com body valido retorna HTTP 202 com JSON { correlationId: UUID, productId: UUID, status: PENDING }
- AC2: Produto com nome valido e preco > 0 e salvo no banco via ProductRepository.save()
- AC3: Apos salvar, produto e publicado na fila RabbitMQ products-input como mensagem JSON
- AC4: POST /api/products com nome vazio retorna HTTP 400 Bad Request com detalhes do erro
- AC5: POST /api/products com preco <= 0 retorna HTTP 400 Bad Request com detalhes do erro
- AC6: ProductMessageConsumer consome mensagens de products-input e republica em products-output
- AC7: Constructor injection em todas as classes (sem @Autowired em campos)
- AC8: Filas RabbitMQ nomeadas products-input e products-output (padrao kebab-case)

## Modulos afetados
_(nenhum identificado)_

## Padroes existentes a seguir
_(nenhum padrao)_

## Estrategia de testes
TDD estrito: para cada AC, escreva teste(s) falhando primeiro, implemente o mínimo para passar, refatore mantendo verdes. Diff coverage mínimo: 90%.

## EXPLICITAMENTE fora de escopo
- Clarificar com o autor se ProductRepository.ProductInfo existente deve migrar para UUID ou se o novo Product coexiste com ProductInfo mantendo String como ID para compatibilidade com OrderServiceImpl.
