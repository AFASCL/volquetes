# Pedidos — Architect (Issue 3, T1–T5)

## Herramienta consultada
Cursor (Agente 4 — Architect)

## Input (prompt)
- Tickets a diseñar: T1 (DB), T2 (Backend CRUD + selectores), T3 (Backend transiciones + permisos), T4 (Frontend ABM), T5 (Frontend estado + asignación).
- Fuente de verdad: memory-bank 00, 01, 04, 06, 07; tech-plan Issue 3.
- Pedido: definir diseño técnico mínimo viable sin inventar campos. Entregables: resumen arquitectura, modelo de datos (tablas, PK/FK, constraints, índices, scripts), contrato API (endpoints, DTOs, errores), reglas de negocio en Service, seguridad mínima, observabilidad, ADRs, diagramas Mermaid, qué actualizar en memory-bank. Sin código.

## Output
- **Resumen:** pedidos con tabla propia y FK a clientes/volquetes/choferes/camiones; unicidad volquete activo con índice único parcial; transiciones en backend; permisos Admin vs Chofer documentados.
- **Modelo de datos:** tablas choferes, camiones, pedidos (campos, FKs, chk_pedidos_estado, uk_pedidos_volquete_activo, índices). Scripts 20260204_3_pedidos.sql y __rollback.
- **Contrato API:** GET/POST/PUT/PATCH pedidos; GET choferes/selector, GET camiones/selector; DTOs y códigos 400/404/409/422.
- **ADRs:** ADR-005 a ADR-008 (estado VARCHAR, unicidad parcial, chofer/camión catálogo, transacción pedido+volquete).
- Documento completo: `docs/arch-pedidos-issue3-t1-t5.md`. Memory-bank 06, 04 y 01 actualizados con tablas pedidos/choferes/camiones, endpoints y ADRs.

## Decisión humana
Diseño aceptado; memory-bank actualizado en la misma sesión (06, 04, 01).

## Impacto en el repo
- Creado `docs/arch-pedidos-issue3-t1-t5.md`. Actualizados `memory-bank/06-data-model.md`, `memory-bank/04-api-documentation.md`, `memory-bank/01-architecture.md` (tablas, API pedidos/choferes/camiones, ADR-005 a ADR-008).
