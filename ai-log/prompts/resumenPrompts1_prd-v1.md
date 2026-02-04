# PRD v1 — Sistema de Volquetes

## Herramienta consultada
ChatGPT — Agente 1 (PRD Challenger), según AGENTS.md.

## Input (prompt)

*(Completar con tu sesión de ChatGPT: pegar acá el prompt que enviaste, por ejemplo el enunciado inicial del sistema de gestión de volquetes y el prompt del Agente 1 PRD Challenger.)*

Ejemplo de estructura del prompt:
- Rol: PRD Challenger (Analista de Requerimientos) — AFA SCL.
- Entrada: enunciado o idea inicial del sistema (gestión de volquetes, inventario, pedidos, choferes, etc.).

## Output (resumen y extractos)

*(Completar con la respuesta de ChatGPT: resumen o extractos relevantes del PRD generado.)*

Lo documentado en el repo indica:
- Definición de flujo principal end-to-end.
- Identificación de preguntas P0.
- Propuesta de estados de pedido y volquete.

## Decisión humana
- El pedido siempre involucra un volquete (v1).
- Identificación del volquete: código interno + QR externo.
- Cobro por servicio con modalidad común y abono.
- Semáforo de pagos: amarillo >3 días, rojo >7 días.
- Dashboard chofer web mobile con login.

## Impacto en el repo
- Actualización de `memory-bank/00-project-overview.md`.
