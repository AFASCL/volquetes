# 07 — Frontend Guidelines (Convenciones FE del repo)

> Objetivo: registrar convenciones específicas del frontend para que todos implementen igual.

## 1) Estructura de carpetas

```
frontend/
├── index.html
├── package.json
├── vite.config.ts
├── tailwind.config.js
├── postcss.config.js
├── .env.example          # VITE_API_BASE_URL (no commitear .env con secretos)
├── src/
│   ├── main.ts
│   ├── App.vue
│   ├── env.d.ts           # tipos para import.meta.env
│   ├── assets/            # CSS global, imágenes
│   ├── components/        # componentes reutilizables
│   ├── composables/        # useSimpleProgress, etc.
│   ├── router/            # index.ts (rutas + guard)
│   ├── services/
│   │   └── api/           # client.ts (cliente HTTP centralizado)
│   ├── stores/            # Pinia (auth, etc.)
│   └── views/             # vistas por ruta (LoginView, Index, HomeView, Error401/404/500)
```

## 2) Router y seguridad (según repo)

- **History:** `createWebHashHistory()` (URLs con `#`).
- **Layout principal:** `Index.vue` como contenedor; rutas protegidas como `children` de `/`.
- **Meta por ruta:**
  - `meta.public: true` — no requiere auth (login, 401, 404, 500).
  - `meta.permisoRequerido: "STRING"` — permiso necesario para acceder.
- **Guard (router.beforeEach):**
  - Token: `localStorage.auth_token`
  - Profile: `localStorage.auth_profile` (JSON con `{ permisos: string[], role?: string }`).
  - Si no hay token y la ruta no es `public` → redirect `/login`.
  - Si hay `meta.permisoRequerido`: `ROLE_ADMIN` bypass; si no, `profile.permisos.includes(meta.permisoRequerido)`.
  - Sin permiso → redirect `/401`.
- **Última ruta:** `localStorage.auth_lastRoute` (guard la guarda antes de ir a login; login redirige aquí tras éxito).

## 3) Navegación por errores HTTP (API client)

- **401** (no autenticado): redirigir a `/login` (hash `#/login`).
- **403** (sin permiso): redirigir a `/401`.
- 404/422/500: mostrar mensaje en UI; no cambiar ruta por defecto.

## 4) Configurar base URL del backend (VITE_API_BASE_URL)

- **Variable de entorno:** `VITE_API_BASE_URL` (solo variables con prefijo `VITE_` se exponen al cliente).
- **Valor por defecto** (si no está definida): `http://localhost:8080`.
- **Dónde configurar:**
  - Archivo `.env` en la raíz de `frontend/` (no commitear si tiene datos sensibles).
  - Archivo `.env.example` con ejemplo: `VITE_API_BASE_URL=http://localhost:8080`.
- **Uso en código:** `import.meta.env.VITE_API_BASE_URL` (tipado en `src/env.d.ts`).
- **Cliente API:** `src/services/api/client.ts` usa esta base para todas las peticiones; sin barra final. Para descargas (CSV/Excel): `apiRequestBlob()` devuelve `Blob`; la vista dispara la descarga con nombre de archivo según contrato (ej. `inventario-volquetes.csv`).

## 5) Organización

- `views/` — pantallas por ruta.
- `components/` — componentes reutilizables.
- `stores/` — Pinia (estado compartido).
- `services/api/` — cliente HTTP y recursos por dominio.
- `composables/` — lógica reutilizable (useSimpleProgress, etc.).

## 6) Tailwind

- Utility-first.
- Extraer componentes cuando se repita patrón.
- Responsive básico (sm/md/lg).

## 7) UX estados (obligatorio)

- Loading / Empty / Error (+ retry).
- Feedback (toast/alert).

## 8) TypeScript

- DTOs tipados.
- Evitar `any`.

---

## 9) Sistema de diseño (estética unificada)

Objetivo: una sola arquitectura visual para construir todo lo que reste. Usar siempre estas convenciones.

### Paleta

- **Fondo app:** `bg-slate-50/80` o `bg-slate-50`.
- **Cards / contenedores:** fondo blanco, borde `border-slate-200/80`, `rounded-xl` o `rounded-2xl`, sombra suave `shadow-sm`.
- **Texto principal:** `text-slate-800`.
- **Texto secundario:** `text-slate-600`.
- **Texto de ayuda / hints:** `text-slate-500 text-xs` (clase `helper-app` en main.css).
- **Primario (acciones):** `primary` = #2563EB (botones principales, enlaces de acción). En Tailwind: `bg-primary`, `ring-primary`, etc.
- **Sidebar:** `#1E293B` (variable o clase).

### Componentes reutilizables

- **Alertas:** `AppAlert` (success / error con icono).
- **Estado vacío:** `AppEmptyState` (icono + mensaje + hint opcional).
- **Estado error + retry:** `AppErrorState`.
- **Error en formulario:** `AppFormError`.
- **Iconos de acción:** `ActionIcons` (plus, pencil, trash, eye, check, x-mark, etc.).

### Clases de aplicación (main.css)

Usar en formularios y listados:

- **Input/select:** `input-app` — borde slate, focus ring primary, rounded-lg.
- **Label:** `label-app` — text-sm font-medium text-slate-700.
- **Texto de ayuda bajo un campo:** `helper-app` — mt-1.5 text-xs text-slate-500.
- **Card contenedor:** `card-app` — rounded-xl border border-slate-200 bg-white shadow-sm.
- **Tabla contenedor:** `table-wrapper-app` — overflow-x-auto rounded-xl border border-slate-200 bg-white.
- **Botón primario:** `btn-primary` — bg-primary, texto blanco, rounded-xl, icono opcional.
- **Botón secundario:** `btn-secondary` — borde slate, fondo blanco, rounded-xl.
- **Botón paginación:** `btn-pagination` — consistente con secundario pero más compacto.

### Modales

- Overlay: `bg-black/50`, `p-4`.
- Contenedor: `rounded-2xl bg-white p-6 shadow-xl`, `max-w-md` o `max-w-lg`.
- Botones pie: icono + texto, `btn-primary` y `btn-secondary`.

### Tablas

- thead: `bg-slate-50`, `text-xs font-medium uppercase text-slate-500`, `px-4 py-3`.
- tbody: `divide-y divide-slate-200`, celdas `px-4 py-3 text-sm text-slate-900` o `text-slate-600`.
- Paginación bajo tabla: borde-t, `px-4 py-2 text-sm text-slate-500`, botones con `btn-pagination`.

### Transiciones

- `transition-app`: duration-200 ease-out para hover/focus en botones y enlaces.
