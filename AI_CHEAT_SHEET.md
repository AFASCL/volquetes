# AI AGENTS – CHEAT SHEET DE USO (AFA SCL)

Qué agente usar, cuándo y cómo invocarlo.
Válido para Cursor 2.0 y para IA fuera de Cursor.

---

## Regla de oro

- Un mensaje = un agente = una tarea
- La IA propone, el humano decide
- Si no hay Issue, no se programa

---

## Mapa rápido: problema → agente

| Necesidad | Agente |
|---------|--------|
| Ordenar ideas / backlog | Agente 0 — Backlog Triage |
| Mejorar requerimientos | Agente 1 — PRD Challenger |
| Entender flujos de negocio | Agente 2 — Use Case Designer |
| Partir en tickets chicos | Agente 3 — Tech Planner |
| Definir arquitectura | Agente 4 — Architect |
| Programar backend | Agente 5 — Builder Backend |
| Programar frontend | Agente 6 — Builder Frontend |
| Revisar PR / QA | Agente 7 — Verifier |

---

## Agentes de planificación (PM / Tech Lead)

### Agente 0 — Backlog Triage & Grooming
Usar para priorizar y ordenar backlog.

Invocación:
```
Usá el AGENTE 0 — Backlog Triage & Grooming.

Entrada:
<ideas / issues / PRD>
```

---

### Agente 1 — PRD Challenger
Usar para detectar huecos y riesgos.

Invocación:
```
Usá el AGENTE 1 — PRD Challenger.

Entrada:
<issue o PRD>
```

---

### Agente 2 — Use Case Designer
Usar para clarificar flujos complejos.

Invocación:
```
Usá el AGENTE 2 — Use Case Designer.

PRD:
<pegar aquí>
```

---

### Agente 3 — Tech Planner
Usar para dividir trabajo en tickets ejecutables.

Invocación:
```
Usá el AGENTE 3 — Tech Planner.

Entrada:
<PRD o Casos de Uso>
```

---

### Agente 4 — Architect
Usar para definir arquitectura y contratos.

Invocación:
```
Usá el AGENTE 4 — Architect.

Entrada:
<PRD final>
```

---

## Agentes de desarrollo (DEV)

Estos agentes están reforzados por .cursor/rules.

### Agente 5 — Builder Backend
Usado por dev backend.

En Cursor:
```
Implementar este ticket:

<issue backend>
```

---

### Agente 6 — Builder Frontend
Usado por dev frontend.

En Cursor:
```
Implementar este ticket:

<issue frontend>
```

---

## Agente de control de calidad

### Agente 7 — Verifier
Usar para PR review y QA.

Invocación:
```
Usá el AGENTE 7 — Verifier.

Entrada:
<PR o resumen de cambios>
```

---

## Dónde vive cada cosa

- Definición de agentes: AGENTS.md
- Reglas automáticas: .cursor/rules
- Planificación: IA explícita (Cursor o fuera)
- Programación: Cursor + rules

---

## Regla final

La IA acelera.  
El humano decide.  
El Project dice la verdad.
