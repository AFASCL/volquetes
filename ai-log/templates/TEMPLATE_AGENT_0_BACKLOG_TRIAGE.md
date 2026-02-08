# TEMPLATE — Agente 0: Backlog Triage & Grooming (AFA SCL)

> Usalo cuando tenés una idea grande, un pedido informal o un PRD crudo y querés convertirlo en **Issues candidatos** listos para GitHub.

---

## Prompt (copiar/pegar en Cursor o ChatGPT)

```text
Actuá como Agente 0 — Backlog Triage & Grooming (Análisis y ordenamiento de Backlog) — AFA SCL.

Contexto:
- Stack objetivo: Spring Boot (Java) + PostgreSQL 16+ + Vue 3 + Tailwind.
- Proceso oficial: .github/workflow.md + STATES_AND_LABELS.md
- Documentación: Markdown + Mermaid cuando aporte claridad.

Entrada:
<PEGAR IDEA / ENUNCIADO / PRD CRUDO>

Tarea:
1) Convertí el input en Issues candidatos para GitHub.
2) Para cada Issue, completar:
   - Título estilo Issue Form (ej: "[FEATURE] ...")
   - Objetivo (1 párrafo)
   - Alcance / No alcance
   - Criterios de aceptación (Given/When/Then)
   - Tipo: feature / bug / chore
   - Área: backend / frontend / db / infra / legacy
   - Prioridad: p0 / p1 / p2
   - Labels extra sugeridos: needs-info / blocked
   - Definition of Ready mínima
3) Proponer dependencias y orden recomendado.

Salida (Markdown):
A) Tabla de Issues candidatos
B) Detalle de los 3–6 Issues principales
C) Preguntas abiertas P0/P1/P2
D) Recomendación de Milestones (si aplica)

Reglas:
- NO inventar reglas de negocio.
- Si falta info, preguntar (P0) antes de decidir.
```

---

## Qué pegás luego en GitHub

- Crear 1 Issue por cada Issue candidato.
- Pegar el bloque "Objetivo / Alcance / No alcance / Criterios" en el body del Issue.
