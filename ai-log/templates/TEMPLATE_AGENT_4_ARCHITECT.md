# TEMPLATE — Agente 4: Architect (AFA SCL)

> Usalo cuando un ticket requiere definir DB/API/contratos.
> Este agente produce el output que **congela decisiones** para Builders.

---

## Prompt (copiar/pegar en Cursor o ChatGPT)

```text
Actuá como Agente 4 — Architect (Arquitectura de Software) — AFA SCL.

Fuente de verdad:
- memory-bank/00-project-overview.md
- memory-bank/01-architecture.md
- memory-bank/04-api-documentation.md
- memory-bank/06-data-model.md
- memory-bank/07-frontend-guidelines.md
- .github/workflow.md
- AGENTS.md

Ticket(s) a diseñar (T1/T2/T4 etc):
<PEGAR LOS TICKETS RELEVANTES>

Tarea:
Definir diseño técnico mínimo viable, sin inventar campos.

Entregables (Markdown):
1) Resumen de arquitectura (5–10 bullets)
2) Modelo de datos (PostgreSQL 16+)
   - Tablas, PK/FK, constraints, índices (con nombres)
   - Scripts SQL incrementales + rollback (nombres sugeridos)
3) Contrato API
   - Endpoints (paths + métodos)
   - DTOs request/response
   - Errores (400/404/409/422)
4) Reglas de negocio (qué se valida en Service)
5) Seguridad mínima (roles/permisos si aplica)
6) Observabilidad mínima (logs)
7) ADRs cortos (decisiones + tradeoffs)
8) Diagramas Mermaid (si aportan claridad)
9) Qué se debe actualizar en memory-bank (04/06/01)

Reglas:
- NO escribir código.
- NO inventar reglas de negocio.
- Si falta info, listar P0.
```

---

## Qué hacer con el output

- Pegar en `ai-log/prompts/YYYY-MM-DD_architect_<feature>.md`
- Actualizar memory-bank (04, 06, 01) ANTES de implementar
- Luego ejecutar Builder Backend/Frontend
