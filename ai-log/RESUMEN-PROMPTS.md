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

## 8. Implementación Backend — API CRUD Volquetes (T2)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 5 (Builder Backend) |
| **Herramienta** | Cursor |
| **Input** | T2 Backend: API CRUD volquetes + listado por estado; fuentes memory-bank 01/04/06, backend.mdc; alcance Tech Plan + Architect; criterios aceptación (admin, filtro estado, 409 duplicado, 404); entregables código + tests + memory-bank si aplica. |
| **Output** | Entidad Volquete, enum VolqueteEstado, DTOs, Repository (findAll + findByEstadoActual + exists unicidad), Service (CRUD + parseEstadoFilter), Controller, GlobalExceptionHandler (400/404/409/422), tests Service y Controller. Sin SQL (T1 ya hecho). |
| **Decisión humana** | Pendiente: aceptar implementación y cerrar T2 en el board. |
| **Detalle** | [prompts/resumenPrompts7_backend_volquetes_t2.md](prompts/resumenPrompts7_backend_volquetes_t2.md) |

---

## 9. Implementación Frontend — ABM Volquetes (T3)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 6 (Builder Frontend) |
| **Herramienta** | Cursor |
| **Input** | T3 ABM Volquetes: listado + alta/edición/baja + QR en UI; fuentes memory-bank 00/04/07, frontend.mdc; alcance vista ABM, modal form, QR desde codigoExterno, loading/empty/error, acceso admin; sin dependencias nuevas. |
| **Output** | types/volquetes.ts, services/api/volquetes.ts, views/VolquetesAbm.vue (listado, modal form, modal QR con api.qrserver.com), ruta volquetes, link Volquetes en Index (solo admin). |
| **Decisión humana** | Pendiente: aceptar implementación y cerrar T3 en el board. |
| **Detalle** | [prompts/resumenPrompts8_frontend_volquetes_t3.md](prompts/resumenPrompts8_frontend_volquetes_t3.md) |

---

## 10. Implementación Backend — Transición de estado Volquetes (T4)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 5 (Builder Backend) |
| **Herramienta** | Cursor |
| **Input** | T4 Backend: Transición de estado + historial; fuentes memory-bank 01/04/06, backend.mdc; alcance endpoint PATCH estado, reglas transiciones, persistir en historial y actualizar estado_actual, origen opcional; criterios aceptación (transiciones válidas, EN_TRANSITO sin pedido, 404/422); entregables código + tests + memory-bank si aplica. |
| **Output** | Domain (OrigenEstado enum, VolqueteEstadoHistorial entity), DTO (VolqueteEstadoRequest), Repository (VolqueteEstadoHistorialRepository), Service (método cambiarEstado con @Transactional), Controller (PATCH /api/volquetes/{id}/estado), tests Service y Controller. Actualización 04-api-documentation.md con nota sobre reglas de transición v1. |
| **Decisión humana** | Pendiente: aceptar implementación y cerrar T4 en el board. |
| **Detalle** | [prompts/resumenPrompts9_backend_volquetes_t4.md](prompts/resumenPrompts9_backend_volquetes_t4.md) |

---

## 11. Implementación Frontend — Vista estado inventario Volquetes (T5)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 6 (Builder Frontend) |
| **Herramienta** | Cursor |
| **Input** | T5 Vista estado inventario + filtros por estado; fuentes memory-bank 00/04/07, frontend.mdc; alcance vista listado/cards con estado actual, filtros por estado (chips), botón Actualizar o polling cada X segundos, loading/empty/error; no alcance WebSockets, cambio de estado desde vista; criterios aceptación (ver estado, filtrar, actualizar manual/polling). |
| **Output** | views/VolquetesEstado.vue (grid cards, chips filtros con colores, botón Actualizar, toggle polling 30s, loading/empty/error), ruta volquetes-estado, link "Estado Inventario" en Index (autenticados). Reutiliza API listVolquetes con filtro estado. |
| **Decisión humana** | Pendiente: aceptar implementación y cerrar T5 en el board. |
| **Detalle** | [prompts/resumenPrompts10_frontend_volquetes_t5.md](prompts/resumenPrompts10_frontend_volquetes_t5.md) |

---

## 12. Implementación Frontend — Reporte export inventario Volquetes (T6)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 6 (Builder Frontend) |
| **Herramienta** | Cursor |
| **Input** | T6 Reportes / export Excel inventario (v1 básico); fuentes memory-bank 00/04/07, frontend.mdc + general.mdc; alcance botón/enlace \"Exportar inventario\" que dispara la descarga CSV usando el endpoint existente `/api/volquetes/export`, sin cambiar el contrato. |
| **Output** | Nuevo helper `apiRequestBlob` en `client.ts`, método `exportarInventario` en `services/api/volquetes.ts`, botón \"Exportar inventario\" en `VolquetesAbm.vue` con estado loading y feedback éxito/error, y actualización de `07-frontend-guidelines.md` documentando el patrón de descargas CSV/Excel (Blob). |
| **Decisión humana** | Pendiente: aceptar implementación y cerrar T6 en el board. |
| **Detalle** | [prompts/resumenPrompts11_frontend_volquetes_t6.md](prompts/resumenPrompts11_frontend_volquetes_t6.md) |

---

## 13. PRD Challenger — Gestión de Pedidos (Issue 3)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 1 (PRD Challenger) |
| **Herramienta** | Cursor |
| **Input** | Issue 3 Gestión de Pedidos (creación + estados + vínculo a volquete); objetivo, alcance, criterios de aceptación e impacto técnico descritos en el Issue. |
| **Output** | Resumen objetivo, alcance/no alcance, preguntas P0/P1/P2, criterios de aceptación mejorados (Given/When/Then), casos borde, NFRs, Definition of Ready, veredicto NEEDS-INFO. Documento completo en docs/prd-pedidos-issue3-challenger-output.md. |
| **Decisión humana** | Resolver P0 para pasar a READY; actualizar Issue con criterios mejorados. |
| **Detalle** | [prompts/resumenPrompts12_prd-challenger_pedidos.md](prompts/resumenPrompts12_prd-challenger_pedidos.md) |

---

## 14. Tech Planner — Gestión de Pedidos (Issue 3, T1–T5)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 3 (Tech Planner) |
| **Herramienta** | Cursor |
| **Input** | Issue 3 READY + output PRD Challenger; pedido: dividir en tickets T1..Tn (1–2 días), tabla resumen + detalle por ticket (objetivo, alcance, criterios Given/When/Then, tareas, labels, DoD). |
| **Output** | T1 DB (choferes, camiones, pedidos), T2 Backend CRUD + selectores, T3 Backend transiciones estado, T4 Frontend ABM, T5 Frontend estado + asignación; dependencias y criterios. Documento: docs/tech-plan-issue3-pedidos-tickets.md. |
| **Decisión humana** | Plan aceptado; cargar tickets en board. |
| **Detalle** | [prompts/resumenPrompts13_tech-planner_pedidos.md](prompts/resumenPrompts13_tech-planner_pedidos.md) |

---

## 15. Architect — Pedidos (Issue 3, T1–T5)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 4 (Architect) |
| **Herramienta** | Cursor |
| **Input** | Tickets T1–T5 + memory-bank 00/01/04/06/07; pedido: diseño técnico mínimo viable (modelo datos, contrato API, reglas negocio, seguridad, ADRs, diagramas); qué actualizar en memory-bank. Sin código. |
| **Output** | docs/arch-pedidos-issue3-t1-t5.md: tablas pedidos/choferes/camiones, scripts 20260204_3_pedidos.sql, contrato API pedidos/choferes/camiones, ADR-005 a ADR-008. Memory-bank 06, 04, 01 actualizados. |
| **Decisión humana** | Diseño aceptado; memory-bank actualizado. |
| **Detalle** | [prompts/resumenPrompts14_architect_pedidos.md](prompts/resumenPrompts14_architect_pedidos.md) |

---

## 16. Backend T1 — DB tablas pedidos, choferes, camiones

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 5 (Builder Backend) |
| **Herramienta** | Cursor |
| **Input** | Ticket T1: DB tablas pedidos, choferes, camiones + constraints (chk_pedidos_estado, uk_pedidos_volquete_activo); script incremental + rollback. Fuente: memory-bank 01/04/06, backend.mdc. |
| **Output** | db/scripts/20260204_3_pedidos.sql (CREATE TABLE choferes, camiones, pedidos; FKs, CHECK, UNIQUE INDEX parcial), 20260204_3_pedidos__rollback.sql. Actualización 06-data-model. |
| **Decisión humana** | Scripts aceptados; ejecución manual. |
| **Detalle** | [prompts/resumenPrompts15_backend_pedidos_t1.md](prompts/resumenPrompts15_backend_pedidos_t1.md) |

---

## 17. Backend T2 — CRUD pedidos + selectores choferes/camiones

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 5 (Builder Backend) |
| **Herramienta** | Cursor |
| **Input** | Ticket T2: API CRUD pedidos (listado filtros, alta, detalle, edición) + GET /api/choferes/selector, GET /api/camiones/selector; validación 409 volquete en pedido activo. Fuente: memory-bank 01/04/06, backend.mdc. |
| **Output** | Entidades Pedido, Chofer, Camion; DTOs; PedidoRepository (existsByVolqueteIdAndEstadoIn); Services y Controllers; GlobalExceptionHandler; tests Service y Controller. |
| **Decisión humana** | Implementación aceptada. |
| **Detalle** | [prompts/resumenPrompts16_backend_pedidos_t2.md](prompts/resumenPrompts16_backend_pedidos_t2.md) |

---

## 18. Frontend T4 — ABM pedidos (listado, alta, edición, detalle)

| Campo | Valor |
|-------|--------|
| **Agente** | Agente 6 (Builder Frontend) |
| **Herramienta** | Cursor |
| **Input** | Ticket T4: vista ABM pedidos (listado paginado + filtros estado/cliente/volquete, alta con cliente + dirección + volquete, edición Admin, detalle); tipos, API client pedidos, ruta /pedidos, menú Admin. Fuente: memory-bank 00/04/07, frontend.mdc. |
| **Output** | types/pedidos.ts, clientes selector (ClienteSelectorItem, listClientesSelector), services/api/pedidos.ts; PedidosAbm.vue (tabla, filtros, modal alta/edición, detalle); router + link Pedidos (Admin). |
| **Decisión humana** | Implementación aceptada. |
| **Detalle** | [prompts/resumenPrompts17_frontend_pedidos_t4.md](prompts/resumenPrompts17_frontend_pedidos_t4.md) |

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
