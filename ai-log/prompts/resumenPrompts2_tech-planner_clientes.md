# Tech Planning — Gestión de Clientes

## Herramienta consultada
Cursor 2.4 — Agente 3 (Tech Planner), según AGENTS.md.

## Input (prompt)

Rol: Tech Planner (Planificación Técnica) — AFA SCL. Convertí el siguiente Issue en tareas técnicas ejecutables de 1–2 días máximo, separadas por backend / frontend / DB. No propongas código todavía.

**Issue:**
Title: [FEATURE] Gestión de Clientes (ABM + tipo común/abono)

Objetivo: Gestionar clientes para asociarlos a pedidos, distinguiendo clientes comunes y de abono.

Alcance: ABM de clientes; tipo común/abono; datos mínimos: nombre/razón social, contacto (tel/email), dirección principal. No incluye: facturación electrónica, múltiples direcciones (v1).

Criterios de aceptación: Admin crea cliente → aparece en listados y puede seleccionarse al crear pedido; cliente "abono" → en pedidos se puede seleccionar modalidad abono; validaciones mínimas (nombre no vacío).

Impacto técnico: Backend CRUD + DTOs + validaciones; Frontend vista ABM + formulario + listado.

## Output (resumen y extractos)

La IA devolvió un plan en Markdown con:

- **Orden de dependencias:** T1 DB → T2 Backend → T3 Frontend ABM → T4 Frontend selector en pedido.
- **Ticket 1 (DB):** Script incremental tabla `clientes`, rollback manual, sin timestamps opcionales.
- **Ticket 2 (Backend):** API CRUD + endpoint selector; DTOs; validaciones; @ControllerAdvice; tests Service y Controller.
- **Ticket 3 (Frontend):** Vista ABM (listado + formulario), API client, estados loading/empty/error.
- **Ticket 4 (Frontend):** Selector de cliente en alta de pedido (dependiente de T3 y de existencia de vista pedidos).

Cada ticket con: título, objetivo, alcance, criterios Given/When/Then, tareas técnicas (DB/backend/frontend/tests), riesgos, labels sugeridos, Definition of Done.

## Decisión humana
Se acepta el plan; se refleja como checklist en el Issue P0 (T1–T4).

## Impacto en el repo
- Issue P0 actualizado con subtareas técnicas.
- Referencia usada para Architect (Agente 4) y Builder Backend/Frontend (Agentes 5 y 6).
