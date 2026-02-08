# TEMPLATE — Agente 2: Use Case Designer (AFA SCL)

> Usalo cuando el PRD ya está READY y querés bajar a **casos de uso** y diagramas.

---

## Prompt (copiar/pegar en Cursor o ChatGPT)

```text
Actuá como Agente 2 — Use Case Designer (Diseño de Casos de Uso) — AFA SCL.

PRD (READY):
<PEGAR PRD FINAL>

Tarea:
- Transformar el PRD en Casos de Uso completos.
- Incluir flujos alternativos y errores.
- Generar diagramas Mermaid cuando aplique.

Salida (Markdown):
A) Lista de Casos de Uso (1 línea cada uno)
B) Para cada Caso de Uso:
   - Actor
   - Precondiciones
   - Flujo principal (pasos numerados)
   - Flujos alternativos
   - Postcondiciones
C) Diagramas Mermaid:
   - sequenceDiagram (mínimo 1 del flujo principal)
   - stateDiagram-v2 si hay estados de negocio

Reglas:
- No inventar pantallas ni endpoints: describir a nivel funcional.
```

---

## Qué hacer con el output

- Guardarlo en `docs/` o en `memory-bank/` si el proyecto lo requiere.
- Referenciarlo en el Issue como link o snippet.
