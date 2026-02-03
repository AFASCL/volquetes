# Resumen de prompts — Volquetes (entrega curso AI Engineer)

Documentación del uso de IA en el proyecto: agente, contexto, input resumido, output y decisión humana.

---

## 1. PRD v1 — Sistema de Volquetes

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 1 (PRD Challenger) |
| **Herramienta** | ChatGPT |
| **Input** | Enunciado inicial del sistema de gestión de volquetes. |
| **Output** | Flujo principal end-to-end, preguntas P0, estados de pedido y volquete. |
| **Decisión humana** | Pedido siempre con volquete (v1); código interno + QR; modalidad común/abono; semáforo pagos; dashboard chofer con login. |
| **Detalle** | [prompts/resumenPrompts1_prd-v1.md](prompts/resumenPrompts1_prd-v1.md) |

---

## 2. Tech Planning — Gestión de Clientes

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 3 (Tech Planner) |
| **Herramienta** | Cursor |
| **Input** | Issue P0: Gestión de Clientes (ABM + tipo común/abono). |
| **Output** | T1 DB, T2 Backend, T3 Frontend ABM, T4 integración con pedidos; dependencias y criterios de aceptación técnicos. |
| **Decisión humana** | Plan aceptado; checklist en el Issue P0. |
| **Detalle** | [prompts/resumenPrompts2_tech-planner_clientes.md](prompts/resumenPrompts2_tech-planner_clientes.md) |

---

## 3. Arquitectura — Clientes

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 4 (Architect) |
| **Herramienta** | Cursor |
| **Input** | Issue Clientes + Tech Plan T1–T4 + restricciones (Spring Boot, PostgreSQL, Vue 3). |
| **Output** | Modelo de datos tabla clientes, contrato API /api/clientes, DTOs, validaciones, seguridad (Admin). |
| **Decisión humana** | Campo único `nombre`; tipo VARCHAR+CHECK; sin unicidad en v1; delete físico; endpoint /selector. |
| **Detalle** | [prompts/resumenPrompts3_architect_clientes.md](prompts/resumenPrompts3_architect_clientes.md) |

---

## 4. Implementación Backend — CRUD Clientes (T2)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 5 (Builder Backend) |
| **Herramienta** | Cursor |
| **Input** | Contrato 04-api-documentation, 06-data-model, T2 (API CRUD + selector). |
| **Output** | Entidad, DTOs, Repository, Service, Controller, GlobalExceptionHandler, tests. |
| **Decisión humana** | Aceptar implementación; luego alineación razonSocial → nombre (DB + backend + memory-bank). |
| **Detalle** | [prompts/resumenPrompts4_backend_clientes.md](prompts/resumenPrompts4_backend_clientes.md) |

---

## Otros usos de IA (sin archivo detallado aún)

- **Esqueleto backend** (Spring Boot 3, Java 21, Maven, health, ControllerAdvice): Agente 5 — Builder Backend.
- **Scripts DB** (tabla clientes, rename razon_social → nombre): Agente 5 — Builder Backend.
- **Alineación contrato** (memory-bank 04 y 06 con campo `nombre`): actualización según Architect/Backend.
- **Esqueleto frontend** (Vue 3, Router, Pinia, Tailwind, Login/Index/Home/Errores, API client): Agente 6 — Builder Frontend.

*(Podés agregar entradas en prompts/ para estos y una fila aquí cuando quieras documentarlos para la entrega.)*
