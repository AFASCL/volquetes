URL de Pruebas para verlo corriendo (render, supabase y vercel): https://volquetes.vercel.app/

# 📦 Volquetes — Proyecto Final (IA Engineer) + Metodología de Desarrollo con Cursor 2.x

Este repositorio es **doble propósito**:

1) **Producto:** construir un sistema funcional para la **gestión integral de una empresa de volquetes** (inventario, pedidos, choferes, pagos, etc.).  
2) **Proceso:** diseñar y aplicar una **metodología profesional de desarrollo asistido por IA** basada en **Cursor 2.0**, con reglas, agentes y documentación viva.

> El objetivo real del Trabajo Final no es solo “programar un sistema”,  
> sino construir un **modelo de ingeniería de software replicable** que luego pueda aplicar en mi trabajo profesional en AFA SCL.

---

## 🎯 Objetivo del sistema (Producto)

El sistema permite administrar una empresa de volquetes que inicia con:
- 1 camión + pluma
- ~30 volquetes (contenedores)
- 2 perfiles: **Admin** y **Chofer**

La plataforma está pensada para crecer a futuro (más camiones, más choferes, más volquetes).

---

## 🧩 Alcance v1 (MVP)

Incluye:
- Gestión de clientes (común / abono)
- Inventario de volquetes (ABM + estados)
- Gestión de pedidos (entrega / retiro)
- Asignación de chofer y camión (v1 mínima)
- Dashboard para chofer (hoja de ruta mobile)
- Control de pagos simple (pago / no pagó)
- Control de permanencia del volquete en cliente (semáforo por días)
- Reportes básicos + exportación a Excel/CSV

No incluye (v2+):
- Optimización avanzada de rutas (ej. camión con 3 volquetes vacíos)
- Firma digital + envío PDF con logo
- Servicios especiales (bobcat, demoliciones, venta de materiales)
- Workflows complejos / permisos granulares
- Multi-camión con logística avanzada

---

## 🧠 Objetivo del repositorio (Proceso)

Este repo implementa un sistema de trabajo que intenta resolver el problema real de la IA aplicada al desarrollo:

- evitar prompts improvisados
- evitar que la IA invente reglas o endpoints
- mantener trazabilidad (Issue → commits → scripts DB → docs → tests)
- lograr que el proceso sea repetible por un equipo real

El resultado es un “framework de trabajo” que puede adoptarse en otros proyectos.

---

## 🧠 Principios clave

- Si no hay Issue, el trabajo no existe.
- No se escribe código sin PRD y tickets definidos.
- La IA **asiste**, no decide.
- Cursor debe obedecer reglas claras (no improvisar).
- El proceso del equipo es la fuente de verdad.
- Las decisiones finales y la responsabilidad siguen siendo humanas.

---

## 🗂️ Estructura del repo (Arquitectura de trabajo)

```text
.github/            → workflow del equipo, estados, labels, issue templates
.cursor/rules/      → reglas automáticas que gobiernan a Cursor (IA)
memory-bank/        → documentación viva del proyecto (API, DB, arquitectura)
AGENTS.md           → catálogo oficial de agentes IA (roles y prompts)
AI_USAGE.md         → cómo se usa IA en el equipo (reglas operativas)
AI_CHEAT_SHEET.md   → guía rápida diaria para trabajar con Cursor
ai-log/             → registro auditable del uso de IA (para el curso)
backend/            → backend Spring Boot (Java) + tests
frontend/           → frontend Vue 3 + TypeScript + Pinia + Tailwind
db/scripts/         → scripts SQL incrementales + rollback manual
```

---

## 🔁 Ciclo de vida del trabajo en GitHub (.github)

El repo está diseñado para que el trabajo fluya siempre por etapas claras:

1. **Idea / Pedido crudo**
2. **Issue creado** (en estado inicial)
3. **PRD refinado** (agente 1)
4. **Casos de uso** (agente 2, si aplica)
5. **Plan técnico (T1..Tn)** (agente 3)
6. **Diseño técnico / Arquitectura** (agente 4)
7. **Implementación** (agentes 5 y 6)
8. **QA / verificación** (agente 7)
9. **Merge / entrega**

> Esto se apoya en `.github/workflow.md` y `.github/STATES_AND_LABELS.md`  
> para que el repositorio sea la fuente de verdad del estado real.

---

## 🤖 Agentes IA (roles oficiales)

| Agente | Rol | Quién lo usa | Resultado |
|------|-----|--------------|----------|
| 0 | Backlog Triage & Grooming | PM / Lead | Ideas → Issues |
| 1 | PRD Challenger | PM / Lead | Issue → PRD sólido |
| 2 | Use Case Designer | PM / Lead | PRD → Casos de uso |
| 3 | Tech Planner | PM / Lead | PRD → T1..Tn (tickets ejecutables) |
| 4 | Architect | PM / Lead | Contratos API + DB + ADR |
| 5 | Builder Backend | Dev Backend | Código backend + tests + SQL |
| 6 | Builder Frontend | Dev Frontend | Código frontend + UX |
| 7 | Verifier (QA) | Lead / QA | Checklist QA + riesgos |

📌 Detalle completo en `AGENTS.md`

---

## ⚙️ Cursor 2.0 — Cómo se usa (día a día)

### Para desarrollar (Backend/Frontend)

1) Abrir el repo en Cursor  
2) Tomar un Issue en estado **In progress**  
3) En el chat de Cursor usar el agente correspondiente (5 o 6) y pegar el ticket:

```text
Actuá como Agente 5 — Builder Backend.

Implementá este ticket:
<pegar Issue/Ticket>
```

o

```text
Actuá como Agente 6 — Builder Frontend.

Implementá este ticket:
<pegar Issue/Ticket>
```

Las reglas en `.cursor/rules/` hacen que Cursor:
- respete arquitectura en capas
- no invente endpoints ni campos
- use DTOs y validaciones
- maneje errores consistentemente
- respete convenciones del router/auth en Vue
- actualice el memory-bank cuando corresponda

👉 La idea es **no escribir prompts largos**, sino que el repositorio ya sea el “prompt”.

---

### Para planificar y diseñar (PM / Tech Lead)

- Los agentes 0–4 y 7 se usan explícitamente.
- Se trabaja principalmente en:
  - Issues de GitHub
  - memory-bank/
  - (opcional) ChatGPT cuando no se está dentro de Cursor

---

## 📚 Memory Bank (documentación viva)

La carpeta `memory-bank/` es el **contexto vivo del proyecto**.  
Cursor la debe leer antes de proponer cambios.

Ejemplos:
- `04-api-documentation.md` → contratos REST y DTOs
- `06-data-model.md` → tablas, constraints, índices, scripts SQL
- `01-architecture.md` → ADRs y decisiones técnicas

---

## 🧾 AI Log (solo para el curso)

Este proyecto incluye un **registro explícito del uso de IA** para trazabilidad académica.

La carpeta `ai-log/` contiene:
- prompts utilizados por agente
- input / output resumido
- decisiones humanas tomadas
- impacto en el código o documentación

📌 Nota: en proyectos reales este log puede ser opcional.  
En este Trabajo Final es parte del entregable y evidencia el proceso.

---

## 🧪 Calidad y tests (mínimo profesional)

El proceso exige:
- Unit tests en Services (backend)
- Integration tests con MockMvc (backend)
- QA manual checklist en frontend (y Vitest si existe)
- Scripts SQL incrementales + rollback manual (sin Flyway/Liquibase)
- Documentación actualizada en el mismo PR

---

## 🚀 Despliegue (Supabase + Render + Vercel)

Para publicar la app en la nube:

- **Base de datos:** Supabase (PostgreSQL)
- **Backend:** Render
- **Frontend:** Vercel (apuntando a la URL del backend en Render)

Guía paso a paso en **[docs/DEPLOY.md](docs/DEPLOY.md)**.

---

## ✅ Regla final

> La IA acelera.  
> El humano decide.  
> El Project dice la verdad.
