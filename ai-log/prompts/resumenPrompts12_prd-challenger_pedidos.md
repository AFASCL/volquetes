# PRD Challenger — Issue 3: Gestión de Pedidos (AFA SCL)

## Herramienta consultada
Cursor (Agente 1 — PRD Challenger)

## Input (prompt)
Issue / PRD crudo: **3 - [FEATURE] Gestión de Pedidos (creación + estados + vínculo a volquete)**

- **Objetivo:** Crear y gestionar pedidos end-to-end, siempre asociados a un volquete.
- **Alcance:** Crear pedido con cliente + dirección de servicio + modalidad precio (común/abono); asociar 1 volquete (obligatorio); estado de pedido: Nuevo → Asignado → Entregado → Retirado → Cerrado / Cancelado. No incluye: pedidos sin volquete; servicios especiales (v2).
- **Criterios de aceptación:** No se puede crear pedido sin cliente y sin volquete; un volquete no puede estar en más de un pedido activo (Nuevo/Asignado/Entregado); el pedido permite avanzar estados en orden (no “Retirado” sin “Entregado”).
- **Impacto técnico:** Backend: modelo + endpoints + validaciones negocio; Frontend: ABM pedidos + cambio de estado controlado; DB: FK y constraints / índices.

Fuente de verdad indicada: memory-bank/00-project-overview.md, .github/workflow.md, STATES_AND_LABELS.md (los dos últimos no existen en el repo; se usó 00, 06-data-model, 04-api-documentation).

## Output
Salida Markdown completa del Agente 1 (objetivo, alcance, P0/P1/P2, criterios Given/When/Then, casos borde, NFRs, DoR, veredicto NEEDS-INFO): ver **docs/prd-pedidos-issue3-challenger-output.md**.

## Decisión humana
Pendiente: responder P0/P1, actualizar Issue y marcar READY cuando se cierren preguntas bloqueantes.

## Impacto en el repo
Ninguno directo; el output es input para Tech Planner y Architect (pedidos).
