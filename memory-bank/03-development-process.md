# 03 — Development Process (Proceso de desarrollo del repo)

> Objetivo: aclarar “cómo trabajamos ESTE repo” sin duplicar la org.
> Referencia principal: `.github/workflow.md`

## 1) Workflow y estados
- Workflow oficial: ver `.github/workflow.md`
- Estados del Project: ver `.github/STATES_AND_LABELS.md`

## 2) Convenciones específicas del repo (si hay)
- Ramas:
- Convención de nombres:
- Scripts DB:
- Rutas FE:

## 3) Uso de IA (resumen operativo)
- Agentes IA: `AGENTS.md`
- Guía de uso: `AI_USAGE.md`
- Cheat sheet: `AI_CHEAT_SHEET.md`

### Regla práctica
- Planificación (Agentes 0–4, 7): invocación explícita.
- Implementación (Agentes 5–6): Cursor + `.cursor/rules`.

## 4) Definition of Done (DoD) mínima del repo
- [ ] Issue con criterios de aceptación
- [ ] Código compilable + lint básico
- [ ] Tests mínimos (según ticket)
- [ ] Script SQL incremental + rollback (si aplica)
- [ ] Docs actualizadas (si cambia API/flujo)
- [ ] PR revisado (si aplica)

## 5) Comandos útiles (placeholder)
### Backend
- Run: `./mvnw spring-boot:run`
- Test: `./mvnw test`

### Frontend
- Run: `npm run dev`
- Build: `npm run build`
