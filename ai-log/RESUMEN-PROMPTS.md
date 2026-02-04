# Resumen de prompts — Volquetes (entrega maestría)

**Requisito:** documentar interacciones con IA: herramienta consultada, input (prompt) y output.  
Cada fila enlaza a una ficha en `prompts/` donde están el **input completo o resumido** y el **output** (o extractos).

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

## 5. Backlog Triage — Inventario de Volquetes

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 0 (Backlog Triage & Grooming) |
| **Herramienta** | Cursor |
| **Input** | Contexto v1 + feature Inventario (ABM + estados + QR); requerimiento crudo: ABM, código interno/externo, estados Disponible/En cliente/En tránsito/Fuera de servicio, vista estado, reportes Excel si aplica. |
| **Output** | Tickets T1–T6 (DB, API CRUD, Frontend ABM, actualización estado, vista estado+filtros, export Excel); criterios Given/When/Then; P0/P1/P2; orden y milestones. |
| **Decisión humana** | Pendiente de revisión. |
| **Detalle** | [prompts/resumenPrompts5_backlog-triage_volquetes.md](prompts/resumenPrompts5_backlog-triage_volquetes.md) |

---

## 6. Tech Planning — Inventario de Volquetes (T1–T6)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 3 (Tech Planner) |
| **Herramienta** | Cursor |
| **Input** | Feature Inventario de Volquetes; output Agente 0 (T1–T6); P0 cerradas: estados DISPONIBLE/EN_CLIENTE/EN_TRANSITO/FUERA_DE_SERVICIO, QR en UI desde codigoExterno, EN_TRANSITO sin pedido, historial de estado. |
| **Output** | Plan técnico ejecutable: T1–T6 con objetivo, alcance, criterios Given/When/Then, tareas técnicas, DoD, labels, dependencias y blocked; Markdown listo para GitHub. |
| **Decisión humana** | Pendiente de cargar en GitHub/board. |
| **Detalle** | [prompts/resumenPrompts6_tech-planner_volquetes.md](prompts/resumenPrompts6_tech-planner_volquetes.md) |

---

## 7. Arquitectura — Inventario de Volquetes (T1 + T2)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 4 (Architect) |
| **Herramienta** | Cursor |
| **Input** | Feature Inventario de Volquetes; fuentes memory-bank + general.mdc; contexto: estados DISPONIBLE/EN_CLIENTE/EN_TRANSITO/FUERA_DE_SERVICIO, QR en UI desde codigoExterno, historial de estado; entregables: modelo de datos, contrato API, ADRs, memory-bank a actualizar. Sin código ni issues. |
| **Output** | docs/arch-inventario-volquetes-t1-t2.md: tablas volquetes y volquete_estado_historial (PK, FK, constraints, índices), CRUD + PATCH estado, DTOs, códigos de error, 3 ADRs (VARCHAR/CHECK, historial separado, estado denormalizado), secciones de 04/06/01 a actualizar. |
| **Decisión humana** | Pendiente: aceptar diseño y actualizar memory-bank al implementar T1/T2. |
| **Detalle** | [prompts/2026-02-03_architect_volquetes.md](prompts/2026-02-03_architect_volquetes.md) |

---

## Otros usos de IA (sin archivo detallado aún)

- **Esqueleto backend** (Spring Boot 3, Java 21, Maven, health, ControllerAdvice): Agente 5 — Builder Backend.
- **Scripts DB** (tabla clientes, rename razon_social → nombre): Agente 5 — Builder Backend.
- **Alineación contrato** (memory-bank 04 y 06 con campo `nombre`): actualización según Architect/Backend.
- **Esqueleto frontend** (Vue 3, Router, Pinia, Tailwind, Login/Index/Home/Errores, API client): Agente 6 — Builder Frontend.

*(Podés agregar entradas en prompts/ para estos y una fila aquí cuando quieras documentarlos.)*

---

## Para la entrega de la maestría

- En cada ficha de **prompts/** completá **Input (prompt)** con el texto real que enviaste (o un resumen fiel) y **Output** con la respuesta de la IA (completa o extractos clave).
- Donde hoy dice solo "Enunciado inicial" o "Issue P0", reemplazá por el prompt tal como lo pegaste en la herramienta.
- Usá **prompts/PLANTILLA-entrada.md** para nuevas interacciones.
- Con eso tenés la evidencia: **a cuál consulté**, **qué le pregunté** y **qué me respondió**.
