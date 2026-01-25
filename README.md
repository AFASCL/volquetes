<<<<<<< HEAD
# volquetes
=======
# 📦 Proyecto — Desarrollo con Cursor + IA

Este repositorio utiliza un **sistema de desarrollo de software asistido por IA**
basado en **Cursor 2.0**, agentes especializados y reglas automáticas.

El objetivo es:
- acelerar el desarrollo
- reducir errores
- mantener trazabilidad y calidad
- evitar improvisación con IA

---

## 🧠 Principios clave

- Si no hay Issue, el trabajo no existe.
- La IA **asiste**, no decide.
- El proceso del equipo es la fuente de verdad.
- Cursor debe obedecer reglas claras (no improvisar).
- No se escribe código sin PRD y tickets definidos.

---

## 🗂️ Estructura clave del repo

```text
.github/            → workflow, estados, issue forms (proceso del equipo)
.cursor/rules/      → reglas automáticas que gobiernan a Cursor (IA)
memory-bank/        → contexto vivo del proyecto (arquitectura, API, decisiones)
AGENTS.md           → catálogo de agentes IA (roles y prompts)
AI_USAGE.md         → cómo usa IA el equipo
AI_CHEAT_SHEET.md   → guía rápida diaria
backend/            → código Spring Boot
frontend/           → código Vue 3
```

---

## 🤖 Agentes IA (resumen)

| Agente | Rol | Quién lo usa |
|------|-----|--------------|
| 0 | Backlog Triage & Grooming | PM / Lead |
| 1 | PRD Challenger | PM / Lead |
| 2 | Use Case Designer | PM / Lead |
| 3 | Tech Planner | PM / Lead |
| 4 | Architect | PM / Lead |
| 5 | Builder Backend | Dev Backend |
| 6 | Builder Frontend | Dev Frontend |
| 7 | Verifier (QA) | Lead / QA |

> Detalle completo en `AGENTS.md`

---

## ⚙️ Uso de Cursor (día a día)

### Para desarrolladores
- Abrir el repo en Cursor
- Tomar un Issue en estado **In progress**
- En el chat de Cursor escribir simplemente:

```text
Implementar este ticket:

<pegar Issue>
```

Las reglas en `.cursor/rules/` hacen que Cursor:
- respete arquitectura
- no invente endpoints
- use DTOs
- siga convenciones de frontend
- consulte `memory-bank/` antes de proponer cambios

👉 **No escribir prompts largos.**

---

### Para PM / Tech Lead
- Usar agentes 0–4 y 7 **de forma explícita** (en Cursor o fuera).
- Refinar Issues antes de pasar a Ready.
- Mantener `memory-bank/` actualizado cuando haya decisiones relevantes.

---

## 📚 Memory Bank

La carpeta `memory-bank/` contiene el **contexto del proyecto**:
- arquitectura
- contratos API
- modelo de datos
- convenciones frontend
- bitácora de decisiones

Cursor debe consultar estos archivos antes de proponer cambios.

---

## 🧠 AI Log (uso de IA)

Este proyecto incluye un **registro explícito del uso de IA** para trazabilidad
y evaluación del proceso de desarrollo.

La carpeta `ai-log/` contiene:
- prompts utilizados (por agente)
- input / output resumido
- decisiones humanas tomadas
- impacto en el código o documentación

Este registro demuestra que la IA:
- asiste al proceso
- no toma decisiones autónomas
- es auditada y controlada por el desarrollador

---

## ✅ Regla final

> La IA acelera.  
> El humano decide.  
> El Project dice la verdad.
>>>>>>> 0d8d742 (chore: bootstrap repo (cursor rules, memory bank, ai-log))
