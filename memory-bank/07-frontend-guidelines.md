# 07 — Frontend Guidelines (Convenciones FE del repo)

> Objetivo: registrar convenciones específicas del frontend para que todos implementen igual.

## 1) Router y seguridad (según repo)
- History: `createWebHashHistory()`
- Layout: `Index.vue` como contenedor + `children`
- Autorización por ruta:
  - `meta.permisoRequerido: "STRING"`
  - Guard:
    - token: `localStorage.auth_token`
    - profile: `localStorage.auth_profile` (JSON)
    - ROLE_ADMIN bypass
    - si no, `profile.permisos.includes(meta.permisoRequerido)`
- Navegación por errores:
  - 401 (no autenticado): `/login`
  - 403 (sin permiso): `/401`
  - 404: `/404`
- Guardar última ruta:
  - `localStorage.auth_lastRoute`

## 2) Organización
- `views/`, `components/`, `stores/`, `services/api/`, `composables/`

## 3) Tailwind
- Utility-first
- Extraer componentes cuando se repita patrón
- Responsive básico (sm/md/lg)

## 4) UX estados (obligatorio)
- Loading / Empty / Error (+ retry)
- Feedback (toast/alert)

## 5) TypeScript
- DTOs tipados
- Evitar `any`
