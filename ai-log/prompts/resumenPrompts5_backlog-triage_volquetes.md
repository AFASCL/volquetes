# Backlog Triage — Inventario de Volquetes

## Herramienta consultada
Cursor — Agente 0 (Backlog Triage & Grooming), según AGENTS.md.

## Input (prompt)

Actuá como Agente 0 — Backlog Triage & Grooming (Análisis y ordenamiento de Backlog) — AFA SCL.

**Contexto:** Sistema de gestión de empresa de volquetes (v1). Stack: Spring Boot + PostgreSQL 16+ (scripts SQL), Vue 3 + Tailwind.  
**Feature a descomponer:** Inventario de Volquetes (ABM + estados + QR externo).  
**Restricciones:** No inventar workflows ni permisos nuevos; v1 un solo camión, un chofer (sistema debe soportar crecer); volquete siempre existe, todo pedido se asocia a un volquete.

**Tarea:** 1) Proponer Issues/tickets candidatos (1–2 días), ordenados por dependencia (T1..Tn). 2) Para cada ticket: título, objetivo, alcance/no alcance, área, labels sugeridos, criterios Given/When/Then. 3) Identificar preguntas faltantes (P0/P1/P2).

**Entrada (requerimiento crudo):**
- ABM de volquetes.
- Cada volquete: código interno existente + código externo nuevo con QR (para pintarlo).
- Estados de inventario: mínimo “Disponible / En cliente / En tránsito / Fuera de servicio”.
- Ver en tiempo real el estado de cada volquete.
- Reportes/export Excel básicos sobre inventario (si aplica en v1).

## Output (resumen y extractos)

La IA devolvió:

**1) Tabla de tickets candidatos (T1–T6):**
- **T1** — DB: tabla volquetes + estados (script incremental + rollback).
- **T2** — Backend: API CRUD volquetes + listado por estado.
- **T3** — Frontend: ABM Volquetes (listado + alta/edición/baja).
- **T4** — Backend: actualización de estado (En cliente / En tránsito / Fuera de servicio).
- **T5** — Frontend: vista estado en tiempo (casi) real + filtros por estado.
- **T6** — Reportes/export Excel inventario (v1 básico), prioridad p2.

**2) Orden y dependencias:** T1 → T2 → T3 (ABM base); T4 → T5 (estados); T6 opcional v1.

**3) Detalle por ticket:** Para cada uno: objetivo, alcance/no alcance, criterios Given/When/Then, labels (tipo/área/prioridad), riesgos/dependencias.

**4) Preguntas abiertas:**
- **P0:** Estados y transiciones exactas; si “En cliente” se setea desde pedidos o manual; unicidad/generación del código externo (QR).
- **P1:** Si export Excel es v1 o p2; si se guarda historial de cambios de estado; permisos por rol.
- **P2:** Si “tiempo real” es polling vs WebSocket; filtros adicionales.

**5) Milestones sugeridos:** M1 ABM base (T1–T3), M2 Estados (T4–T5), M3 Reportes (T6).

## Decisión humana
*(Pendiente: aceptar plan, ajustar alcance o prioridades, cerrar P0 con negocio.)*

## Impacto en el repo
- Referencia para próximo paso: Architect (modelo de datos y API volquetes) y luego Tech Planner detallado o implementación T1/T2.
