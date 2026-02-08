# TEMPLATE — Agente 3: Tech Planner (AFA SCL)

> Usalo para convertir un Issue READY en una **lista de tickets T1..Tn ejecutables (1–2 días)**.

---

## Prompt (copiar/pegar en Cursor o ChatGPT)

```text
Actuá como Agente 3 — Tech Planner (Planificación Técnica) — AFA SCL.

Fuente de verdad:
- memory-bank/00-project-overview.md
- .github/workflow.md
- STATES_AND_LABELS.md
- AGENTS.md

Issue READY (feature):
<PEGAR ISSUE COMPLETO>

Tarea:
1) Dividir el Issue en tickets T1..Tn de 1–2 días máximo.
2) Cada ticket debe incluir:
   - Título
   - Objetivo
   - Alcance / No alcance
   - Criterios de aceptación (Given/When/Then)
   - Tareas técnicas (bullets)
   - Labels sugeridos (tipo/área/prioridad)
   - Dependencias (T1 -> T2)
   - Definition of Done

Salida (Markdown):
A) Tabla resumen T1..Tn
B) Detalle completo de cada ticket

Reglas:
- No inventar requisitos.
- Si falta info, hacer preguntas P0.
- El resultado debe poder copiarse directo a GitHub como Issues separados.
```

---

## Qué hacer con el output

- Crear un Issue por cada Tx (T1, T2, ...).
- Linkearlos al Issue padre (feature) o al Milestone.
