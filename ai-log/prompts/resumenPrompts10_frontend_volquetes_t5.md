# T5 — Frontend: Vista estado inventario + filtros por estado (Builder Frontend)

## Herramienta consultada
Cursor — Agente 6 (Builder Frontend), según AGENTS.md.

## Input (prompt)

Actuá como **Agente 6 — Builder Frontend (Vue 3 + Tailwind)**.

Fuente de verdad obligatoria: memory-bank/00-project-overview.md, 04-api-documentation.md, 07-frontend-guidelines.md, frontend.mdc + general.mdc.

Objetivo: Implementar el ticket **T5 Vista estado inventario + filtros por estado**.

Alcance: Vista o sección "Estado del inventario": listado/cards de volquetes con estado actual. Filtros por estado (combo o chips); botón "Actualizar" o polling cada X segundos. Estados loading y error; consumo del listado con filtro estado (API T2/T4). No alcance: WebSockets/push en v1 (solo polling o recarga manual), reportes/Excel (T6), cambio de estado desde esta vista (opcional).

Criterios de aceptación: usuario con permiso abre vista → ve volquetes con estado actual; selecciona filtro por estado → solo muestra volquetes en ese estado; actualiza (botón o polling) → ve datos actualizados desde backend.

Tareas técnicas: Vista o ruta dedicada; llamada a API listado con query param de estado; filtros en UI; polling opcional (setInterval o similar); manejo loading/error. DoD: vista operativa, filtros funcionando, datos alineados con backend, actualización manual o por polling verificada.

Repo: frontend/, Router createWebHashHistory + Index.vue con children, auth en localStorage. Restricciones: NO inventar campos ni permisos; API client centralizado; UX obligatorio (loading, empty, error+retry); Tailwind + accesibilidad; no dependencias nuevas.

Entregables: ruta + vista(s) + componentes, service API + types TS, integración en menú/layout si existe patrón. Salida: Supuestos, Resumen, Archivos, Código, Checklist UX, QA manual, Riesgos.

## Output (resumen y extractos)

La IA implementó en el repo:

- **Vista:** VolquetesEstado.vue — listado en grid responsive (cards) con código interno, código externo y estado actual; chips clickeables para filtros por estado (Todos + 4 estados) con colores activos/inactivos; badge de estado con color según estado (verde/azul/amarillo/rojo); botón "Actualizar" manual; toggle "Auto-actualizar (30s)" con polling opcional cada 30 segundos (setInterval, limpia en onUnmounted); estados loading, empty (mensaje según filtro), error + Reintentar; contador "Mostrando X de Y volquetes".
- **Router:** ruta hija `volquetes-estado` → VolquetesEstado.vue (sin meta.permisoRequerido; hereda del padre).
- **Index.vue:** link "Estado Inventario" con `v-if="auth.isAuthenticated"` (visible para usuarios autenticados, no solo admin).
- **API:** reutiliza `listVolquetes` de services/api/volquetes.ts con parámetro `estado` opcional (size: 1000 para obtener todos filtrados en v1).

Memory-bank no se modificó (contrato API ya documentado; sin cambios de convenciones).

## Decisión humana
(Pendiente: aceptar implementación y marcar T5 como cerrado en el board.)

## Impacto en el repo
- Frontend: views/VolquetesEstado.vue; router/index.ts (ruta volquetes-estado); views/Index.vue (link Estado Inventario para autenticados).
