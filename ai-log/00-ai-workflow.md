# AI Log — Volquetes

Este directorio registra el uso de IA para trazabilidad del proyecto.  
**Requisito maestría:** documentar **interacciones con las distintas IA**: a cuál se consultó, **input (prompt)** y **output** (completo o extracto relevante).

## Estructura

- **00-ai-workflow.md** — este archivo (propósito, reglas, plantilla).
- **RESUMEN-PROMPTS.md** — índice de todas las interacciones (para la entrega).
- **prompts/** — una ficha por interacción con: **herramienta consultada**, **input (prompt)**, **output**, decisión humana.
- **decisions/** — ADRs o decisiones técnicas (opcional).

## Qué debe tener cada entrada en prompts/

Para cumplir el requisito de la maestría, cada archivo en `prompts/` debe incluir:

| Campo | Descripción |
|-------|-------------|
| **Herramienta consultada** | Qué IA usaste: Cursor 2.4, ChatGPT, Claude, etc. |
| **Input (prompt)** | El texto que enviaste a la IA (completo o resumido si es muy largo). |
| **Output** | Lo que devolvió la IA: resumen + extractos relevantes o enlace a captura. |
| **Decisión humana** | Qué aceptaste, rechazaste o modificaste. |
| **Fecha / Agente** | Opcional: fecha y rol (ej. Agente 3 Tech Planner). |

Así tenés evidencia de: **a cuál consultaste**, **qué preguntaste** y **qué respondió**.

## Plantilla para nuevas entradas

Copiá esto en un nuevo `.md` en `prompts/` (ej. `YYYY-MM-DD_agente_tema.md`):

```markdown
# [Tema] — [Breve descripción]

## Herramienta consultada
(Cursor / ChatGPT / Claude / otra — versión si aplica)

## Input (prompt)
(Pegar o resumir el texto que enviaste a la IA.)

## Output
(Resumen de la respuesta + extractos relevantes o "Ver captura/archivo X".)

## Decisión humana
(Qué adoptaste o descartaste.)

## Impacto en el repo
(Archivos creados/modificados, si aplica.)
```

## Regla

Cada vez que uses IA para PRD, arquitectura, diseño DB o implementación relevante:

1. Crear (o completar) una ficha en **prompts/** con input, output y herramienta.
2. Añadir una fila en **RESUMEN-PROMPTS.md** que enlace a esa ficha.

## Acuerdo con Cursor (opción 3)

Al cerrar una sesión que sea **entregable** (feature implementado, decisión tomada, diseño hecho, etc.), la IA en Cursor **ofrecerá** documentar esa iteración en ai-log para la maestría:

- *"¿Querés que dejemos esta iteración documentada en `prompts/` para la entrega?"*

Si aceptás, se agrega (o actualiza) una ficha en **prompts/** con input + output y, si aplica, una línea en **RESUMEN-PROMPTS.md**. Así no hace falta que lo recuerdes vos cada vez.
