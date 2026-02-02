# Arquitectura — Clientes (PRD v1)

## IA utilizada
Cursor 2.4x — **Agente 4 (Architect)**

## Contexto
Definición de la arquitectura técnica para el feature **Gestión de Clientes** del sistema de Volquetes (v1), a partir de:
- Issue P0: Gestión de Clientes (ABM + tipo común/abono)
- Tech Plan (T1 DB, T2 Backend, T3 Frontend, T4 Integración)

## Input entregado a la IA
- Issue P0 completo (objetivo, alcance, criterios de aceptación)
- Tech Plan T1–T4
- Restricciones del proyecto (Spring Boot, PostgreSQL 16+, Vue 3)
- Reglas de no-invención y alcance v1 (pago manual, cliente común/abono)

## Output generado por la IA
- Modelo de datos para **tabla clientes** (campos, constraints, índices)
- Contrato API REST para **/api/clientes** (CRUD + selector)
- DTOs request/response y reglas de validación
- Reglas de seguridad (Admin gestiona Clientes; Chofer no)
- Decisiones arquitectónicas documentadas (ADR corto)

## Decisiones humanas tomadas
- Aceptar un único campo `nombre` (nombre/razón social) en v1.
- Implementar `tipo` como VARCHAR + CHECK (no enum nativo de PostgreSQL).
- No definir unicidad de nombre en v1.
- Mantener eliminación física de clientes en v1.
- Separar endpoint liviano `/selector` para combos.

## Impacto en el repositorio
- Actualización de `memory-bank/04-api-documentation.md` (sección Clientes)
- Actualización de `memory-bank/06-data-model.md` (tabla clientes)

## Próximo paso
Implementación técnica con:
- **Agente 5 — Builder Backend** (T1 DB + T2 API)
- Posteriormente **Agente 6 — Builder Frontend**

