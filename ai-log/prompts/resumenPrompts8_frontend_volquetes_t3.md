# T3 — Frontend: ABM Volquetes (listado + alta/edición/baja + QR) (Builder Frontend)

## Herramienta consultada
Cursor — Agente 6 (Builder Frontend), según AGENTS.md.

## Input (prompt)

Actuá como **Agente 6 — Builder Frontend (Vue 3 + Tailwind)**.

Fuente de verdad obligatoria: memory-bank/00-project-overview.md, 04-api-documentation.md, 07-frontend-guidelines.md, frontend.mdc + general.mdc.

Objetivo: Implementar el ticket **T3 ABM Volquetes (listado + alta/edición/baja)**.

Alcance: Vista ABM bajo layout existente (Index): listado con código interno, código externo, estado, acciones. Modal o página de alta/edición: código interno, código externo, estado inicial; validación UI; feedback de errores (ej. 409). Generación de QR en UI a partir de codigoExterno (mostrar o descargar; librería existente o estándar del proyecto). Estados loading, empty, error con retry; confirmación antes de borrar; acceso según permisos (ej. admin). No alcance: cambio de estado desde esta pantalla (T5/T4), reportes/Excel (T6), impresión de etiquetas físicas.

Criterios de aceptación: admin entra a vista volquetes → listado o empty y puede abrir formulario de alta; alta con códigos y estado inicial → se crea y feedback de éxito; edición y guardar → datos actualizados; código externo guardado → se puede ver/generar QR en UI; error 409 → mensaje claro.

Tareas técnicas: Ruta/vista ABM; servicio API volquetes (clientes de T2); tipos TS alineados al contrato; formulario con validación; listado con columnas y acciones; componente o utilidad para QR desde codigoExterno; respetar router/auth y API client; DoD: ABM operable, loading/empty/error, feedback, QR desde codigoExterno.

Repo: frontend/, Router createWebHashHistory + Index.vue con children, auth en localStorage (auth_token / auth_profile). Restricciones: NO inventar campos ni permisos; API client centralizado; UX obligatorio (loading, empty, error+retry, feedback); Tailwind + accesibilidad; no dependencias nuevas salvo existentes.

Entregables: ruta + vista(s) + componentes, service API + types TS, integración en menú/layout si existe patrón (link solo ROLE_ADMIN). Salida: Supuestos, Resumen, Archivos, Código, Checklist UX, QA manual, Riesgos.

## Output (resumen y extractos)

La IA implementó en el repo:

- **Tipos:** `types/volquetes.ts` — VolqueteEstado, VolqueteRequest, VolqueteResponse, VolquetesPageResponse, ErrorResponse (contrato 04).
- **API:** `services/api/volquetes.ts` — listVolquetes (page, size, estado opcional), getVolquete, createVolquete, updateVolquete, deleteVolquete; getErrorMessage con 409 explícito (“Ya existe un volquete con ese código interno o externo”).
- **Vista:** `views/VolquetesAbm.vue` — listado (código interno, código externo, estado con etiquetas, acciones Ver QR / Editar / Eliminar); modal alta/edición (código interno, código externo, estado inicial solo en alta); modal QR con imagen vía api.qrserver.com (sin dependencia nueva) y enlace “Abrir/descargar imagen”; loading, empty, error + Reintentar; feedback success/error; confirmación borrado; paginación; ensureAdmin() en onMounted.
- **Router:** ruta hija `volquetes` → VolquetesAbm.vue.
- **Index.vue:** enlace “Volquetes” con `v-if="auth.hasRoleAdmin"`.

QR: sin librería nueva; URL `https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=...` como img y enlace. Memory-bank no se modificó.

## Decisión humana
(Pendiente: aceptar implementación y marcar T3 como cerrado en el board.)

## Impacto en el repo
- Frontend: types/volquetes.ts, services/api/volquetes.ts, views/VolquetesAbm.vue; router/index.ts (ruta volquetes); views/Index.vue (link Volquetes para admin).
