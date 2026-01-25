# AI_USAGE.md
## Uso de IA (Cursor 2.0) en el proceso de desarrollo AFA SCL

Este documento define **cómo usar IA (Cursor 2.0)** dentro del workflow oficial de AFA SCL, sin reemplazar la responsabilidad humana ni el proceso existente.

La IA **asiste**, **acelera** y **mejora calidad**.  
Las decisiones, prioridades y entregas **siguen siendo humanas**.

---

## 1. Principios fundamentales

- **Si no hay Issue, el trabajo no existe**
- **El Project es la fuente de verdad del estado**
- **La IA no decide prioridades ni alcance**
- **La IA no reemplaza al desarrollador**
- **Todo lo que llega a PROD es revisado por una persona**
- **Simplicidad y claridad > automatización ciega**

---

## 2. Rol de la IA en AFA SCL

La IA se usa como **agentes especializados por etapa**, invocados manualmente desde Cursor 2.0.

Cada agente:
- tiene un propósito claro
- produce un output concreto (texto, checklist, código, tests)
- **no actúa solo**: siempre hay un responsable humano

---

## 3. Agentes oficiales y cuándo usarlos

### 🟢 Agente 0 — Backlog Triage & Grooming
**Cuándo:**  
- Ideas nuevas
- Pedidos informales
- PRDs grandes
- Backlog desordenado

**Qué hace:**
- Propone Issues candidatos
- Mejora objetivos y descripciones
- Sugiere labels, prioridad y DoR
- Divide temas grandes en trabajo manejable

**Qué NO hace:**
- No crea Issues en GitHub
- No decide prioridades finales

**Responsable humano:** PM / Director

---

### 🟢 Agente 1 — PRD Challenger (Analista de Requerimientos)
**Cuándo:**  
- Todo Issue de tipo feature antes de pasar a `Ready`

**Qué hace:**
- Detecta huecos y ambigüedades
- Propone criterios de aceptación
- Identifica edge cases y NFRs
- Define si el Issue está `READY` o `NEEDS-INFO`

**Qué NO hace:**
- No inventa requisitos
- No cambia el alcance sin aprobación

**Responsable humano:** PM (con aporte de devs)

---

### 🟢 Agente 2 — Use Case Designer
**Cuándo:**  
- Issue `READY`
- Features con flujo de negocio

**Qué hace:**
- Casos de uso claros
- Diagramas Mermaid (sequence / state)
- Reglas de negocio explícitas

**Responsable humano:** PM / Arquitecto

---

### 🟢 Agente 3 — Tech Planner
**Cuándo:**  
- Antes de comenzar desarrollo
- Para dividir trabajo en tickets claros

**Qué hace:**
- Convierte PRD/UC en tickets ejecutables
- Define tareas backend/frontend/db/qa/docs
- Propone Definition of Done

**Responsable humano:** PM + Devs

---

### 🟢 Agente 4 — Architect
**Cuándo:**  
- Cambios en API, DB, seguridad o integraciones
- Features estructurales

**Qué hace:**
- Define contratos API
- Modelo de datos
- Decisiones técnicas (ADR)
- Observabilidad y seguridad mínima

**Responsable humano:** PM / Arquitecto

---

### 🟢 Agente 5 — Builder Backend (Spring)
**Cuándo:**  
- Estado `In progress`
- Tickets backend

**Qué hace:**
- Propone código Spring (controllers, services, repos)
- DTOs y validaciones
- Tests mínimos
- Scripts SQL (PostgreSQL 16+)

**Qué NO hace:**
- No mergea código
- No decide arquitectura

**Responsable humano:** Dev Backend

---

### 🟢 Agente 6 — Builder Frontend (Vue 3 + Tailwind)
**Cuándo:**  
- Estado `In progress`
- Tickets frontend

**Qué hace:**
- Componentes Vue 3 (Composition API)
- TypeScript + Pinia
- Tailwind CSS
- Manejo de loading/error/empty
- Tests (si aplica)

**Responsable humano:** Dev Frontend

---

### 🟢 Agente 7 — Verifier (QA + PR Review)
**Cuándo:**  
- Antes de pasar a `In review`
- En `Deployed to TST`
- Antes de PROD

**Qué hace:**
- Checklist de PR
- Plan de QA para TST
- Detección de riesgos
- Validación de DoD

**Responsable humano:** Dev / PM

---

## 4. Roles humanos en el equipo (3 personas)

### 👤 PM / Director
- Prioriza backlog
- Decide alcance y orden
- Crea Issues reales
- Resuelve preguntas P0
- Aprueba Ready y Ready for PROD

### 👤 Dev Backend
- Implementa backend con IA
- Ajusta y valida código generado
- Responsable del PR backend

### 👤 Dev Frontend
- Implementa frontend con IA
- Ajusta UX
- Responsable del PR frontend

---

## 5. Flujo operativo estándar con IA

1. Idea / pedido
2. Agente 0 → Issues candidatos
3. PM crea Issue (feature.yml)
4. Agente 1 → Issue READY / NEEDS-INFO
5. Agente 2/3/4 → UC / tickets / diseño
6. Devs trabajan en ramas `t<issueId>`
7. Agente 7 → auto-review + QA plan
8. Integración a `dev`
9. Deploy manual a TST → QA
10. PR `dev → main` → PROD

---

## 6. Regla final

> **La IA acelera.  
> El humano decide.  
> El Project dice la verdad.**
