# AI Log — Volquetes

Este directorio registra el uso de IA (Cursor + agentes) para trazabilidad del proyecto.  
**Este proyecto es una entrega para un curso de AI Engineer:** el resumen de prompts documenta cómo se usó la IA en cada fase.

## Estructura

- **ai-log/00-ai-workflow.md** — este archivo (propósito y reglas).
- **ai-log/RESUMEN-PROMPTS.md** — resumen consolidado de todos los prompts (para la entrega del curso).
- **ai-log/prompts/** — detalle por sesión: prompt usado, input/output, decisión humana (fecha + agente).
- **ai-log/decisions/** — ADRs o decisiones técnicas importantes (opcional).

## Regla

Cada vez que se use IA para PRD, backlog, arquitectura, diseño DB o implementación relevante, se agrega:

1. Una entrada en **prompts/** con fecha y agente (ej. `YYYY-MM-DD_agenteX_tema.md`).
2. Una línea en **RESUMEN-PROMPTS.md** que apunte a esa entrada.

Así el resumen queda como índice para la entrega del curso y cada archivo en prompts/ es la evidencia detallada.
