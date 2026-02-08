# Volquetes — Implementación Frontend T6 (export inventario)

## Herramienta consultada
Cursor (Agente 6 — Builder Frontend, Vue 3 + Tailwind)

## Input (prompt)
- Contexto general del proyecto: Sistema de Gestión de Volquetes (v1), con backend Spring Boot, frontend Vue 3 + TypeScript + Pinia + Tailwind, y PostgreSQL.
- Fuente de verdad obligatoria: `memory-bank/00-project-overview.md`, `memory-bank/04-api-documentation.md`, `memory-bank/07-frontend-guidelines.md`, reglas `frontend.mdc` y `general.mdc`.
- Pedido específico: **Actuar como Agente 6 — Builder Frontend** para implementar el ticket **T6 Reportes / export Excel inventario (v1 básico)** en el frontend.
- Alcance del ticket:
  - Exponer en la UI un botón o enlace **“Exportar inventario”** que dispare la descarga del inventario completo de volquetes en formato Excel/CSV, para uso operativo/auditoría.
  - Respetar contrato backend existente `GET /api/volquetes/export` (CSV) documentado en `memory-bank/04-api-documentation.md` (no cambiar contrato).
  - Respetar router (`createWebHashHistory`, `Index.vue` con children), auth via `localStorage.auth_token` / `auth_profile`, y patrón de permisos (admin para ABM de volquetes).
  - Respetar restricciones: no inventar campos ni permisos, API client centralizado en `services/api/*`, UX obligatoria (loading, empty, error+retry, feedback success/fail), Tailwind + accesibilidad básica, sin dependencias nuevas.
- Entregables pedidos en el prompt:
  1) Ruta + vista(s) + componentes (pasteables).
  2) Service API + tipos TS alineados al contrato.
  3) Integración en menú/layout solo si existe patrón.
  4) Actualización de memory-bank solo si corresponde (`07-frontend-guidelines.md` y/o `04-api-documentation.md`).
- Formato de salida requerido: secciones 0) Supuestos + P0 bloqueantes, A) Resumen de implementación, B) Archivos creados/modificados, C) Código por archivo, D) Checklist UX estados, E) QA manual, F) Riesgos/pendientes.

## Output

**Resumen de la respuesta de la IA:**
- Validación inicial de fuentes de verdad (`memory-bank/00`, `04`, `07`, `frontend.mdc`, `general.mdc`) y del contrato existente `GET /api/volquetes/export` (CSV con cabeceras fijas y filename `inventario-volquetes.csv`).
- Decisión de ubicar el botón **“Exportar inventario”** en la vista de ABM de volquetes (`VolquetesAbm.vue`), ya que es la pantalla principal de inventario y ya está restringida a admin.
- Diseño de una estrategia para manejar respuestas **no JSON** en el cliente HTTP centralizado (CSV/blob) sin romper el patrón existente de `apiRequest<T>()`.

**Extractos clave (diseño e implementación):**
- **Cliente API (`client.ts`):**
  - Se agrega una nueva función `apiRequestBlob(path, options)` que:
    - Usa la misma base URL (`VITE_API_BASE_URL`), headers y token `Authorization: Bearer <token>` que `apiRequest`.
    - Mantiene la lógica de navegación ante errores de autenticación:
      - `401` → redirige a `#/login` y lanza `Error('No autenticado')`.
      - `403` → redirige a `#/401` y lanza `Error('Sin permiso')`.
    - Para otros estados:
      - Si `!res.ok`, lee el cuerpo como `text()`, intenta parsear JSON para extraer `message`, y lanza un `Error` con ese mensaje o `Error ${res.status}`.
      - Si `res.ok`, devuelve `{ data: await res.blob(), status }`, permitiendo manejar CSV/Excel u otros binarios.
- **Servicio Volquetes (`services/api/volquetes.ts`):**
  - Se importa `apiRequestBlob` desde `client.ts`.
  - Se agrega el método:
    - `export async function exportarInventario(): Promise<Blob>` que llama a `apiRequestBlob('/api/volquetes/export', { method: 'GET' })` y devuelve el `Blob` resultante, sin modificar el contrato del backend.
- **Vista ABM Volquetes (`views/VolquetesAbm.vue`):**
  - Se agrega estado local:
    - `const exportLoading = ref(false)` para controlar el estado de carga del botón de exportación.
  - Se define una constante de nombre de archivo:
    - `const EXPORT_FILENAME = 'inventario-volquetes.csv'` alineada con el contrato documentado.
  - Se implementa una función utilitaria `triggerDownload(blob, filename)` que:
    - Crea un `ObjectURL` con `URL.createObjectURL(blob)`.
    - Crea dinámicamente un `<a>` oculto con `download=filename`, lo inserta en el DOM, dispara `.click()`, lo remueve y finalmente hace `URL.revokeObjectURL(url)`.
  - Se implementa `async function exportarInventario()` que:
    - Setea `exportLoading.value = true`.
    - Llama a `volquetesApi.exportarInventario()` para obtener el `Blob`.
    - Llama a `triggerDownload(blob, EXPORT_FILENAME)` para disparar la descarga.
    - Usa el helper existente `setFeedback('success' | 'error', message)` para mostrar feedback:
      - Éxito: `setFeedback('success', 'Inventario exportado.')`.
      - Error: `setFeedback('error', e.message || 'Error al exportar.')`.
    - En `finally`, vuelve a `exportLoading.value = false`.
  - En el template, en el header de la vista:
    - Se reemplaza el botón único de “Nuevo” por un contenedor flex con:
      - Botón secundario “Exportar inventario”:
        - Clases Tailwind para botón neutro con borde.
        - `:disabled="exportLoading"` y `aria-label="Exportar inventario a CSV"`.
        - Texto dinámico: `exportLoading ? 'Exportando...' : 'Exportar inventario'`.
        - `@click="exportarInventario"`.
      - Botón primario “Nuevo” (creación de volquete) igual al existente.
  - Se mantiene la UX previa de la vista (loading de listado, empty state, error+retry, feedback de mutaciones) sin cambios.
- **Memory-bank (`07-frontend-guidelines.md`):**
  - Se agrega una línea a las guías de frontend para documentar la convención de descargas:
    - “Para descargas (CSV/Excel): `apiRequestBlob()` devuelve `Blob`; la vista dispara la descarga con nombre de archivo según contrato (ej. `inventario-volquetes.csv`).”

## Decisión humana
- Aceptar la estrategia de:
  - Centralizar la lógica de descargas no JSON en `apiRequestBlob()` dentro del cliente API, manteniendo la consistencia con `apiRequest<T>()` para autenticación y manejo de 401/403.
  - Exponer la funcionalidad de exportación desde la vista de ABM de volquetes, reutilizando el patrón de feedback ya existente, sin introducir nuevas rutas ni cambios en el menú.
  - No modificar el contrato backend ni los DTOs, dado que `GET /api/volquetes/export` ya está documentado en `memory-bank/04-api-documentation.md`.
- Pendiente de la entrega: revisar manualmente el CSV descargado y cerrar el ticket T6 en el board de proyecto si la funcionalidad cumple el PRD y los criterios de aceptación.

## Impacto en el repo
- **Frontend (`/frontend`):**
  - `src/services/api/client.ts`  
    - Nuevo helper `apiRequestBlob(path, options)` para manejar respuestas tipo `Blob` (CSV/Excel), con la misma política de auth y navegación de `apiRequest`.
  - `src/services/api/volquetes.ts`  
    - Nuevo método `exportarInventario()` que encapsula la llamada a `GET /api/volquetes/export` devolviendo un `Blob`.
  - `src/views/VolquetesAbm.vue`  
    - Nuevas referencias y lógica:
      - Estado `exportLoading`.
      - Constante `EXPORT_FILENAME`.
      - Función `triggerDownload(blob, filename)`.
      - Función `exportarInventario()` con manejo de errores y feedback.
    - Template: botón adicional “Exportar inventario” con estado de carga y accesibilidad básica.
- **Documentación (`/memory-bank`):**
  - `memory-bank/07-frontend-guidelines.md`  
    - Se documenta el patrón oficial para descargas CSV/Excel desde el frontend usando `apiRequestBlob()` y descarga explícita en la vista.

