# Pedidos — Tech Planner (Issue 3, T1–T5)

## Herramienta consultada
Cursor (Agente 3 — Tech Planner)

## Input (prompt)
- Issue READY: 3 [FEATURE] Gestión de Pedidos (creación + estados + vínculo a volquete), con objetivo, alcance, criterios de aceptación e impacto técnico.
- Refinamiento con Agente 1 (PRD Challenger): respuestas a P0/P1/P2 (dirección de entrega puede ser otra; volquete no obligatorio DISPONIBLE al crear; cancelar desde Nuevo/Asignado; Cerrado solo desde Retirado; modalidad del cliente; Chofer puede cambiar estado; Asignado = chofer + camión + fecha; Admin edita; fechas sí; solo cancelado).
- Pedido: actuar como Agente 3 — Tech Planner; dividir en tickets T1..Tn de 1–2 días; cada ticket con título, objetivo, alcance/no alcance, criterios Given/When/Then, tareas técnicas, labels, dependencias, DoD. Salida: tabla resumen + detalle completo por ticket (listo para GitHub).

## Output
- **Tabla resumen:** T1 DB (choferes, camiones, pedidos + constraints), T2 Backend CRUD pedidos + selectores choferes/camiones, T3 Backend transiciones estado + permisos, T4 Frontend ABM pedidos, T5 Frontend cambio estado + asignación. Dependencias: T1 → T2 → T3; T2 → T4; T3+T4 → T5.
- **Detalle completo** de cada ticket en Markdown (objetivo, alcance, criterios, tareas, labels, DoD). Documento: `docs/tech-plan-issue3-pedidos-tickets.md`.

## Decisión humana
Plan aceptado como base para implementación; cargar tickets en board y asignar al implementar.

## Impacto en el repo
- Creado `docs/tech-plan-issue3-pedidos-tickets.md` (plan técnico T1–T5). No modifica código ni memory-bank directamente.
