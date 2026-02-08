# Tech Planning — Inventario de Volquetes (T1–T6)

## Herramienta consultada
Cursor — Agente 3 (Tech Planner), según AGENTS.md.

## Input (prompt)

Actuá como Agente 3 — Tech Planner (Planificación Técnica) — AFA SCL.

**Entrada:**
- Feature: Inventario de Volquetes (ABM + estados + QR externo)
- Output del Agente 0 (tickets T1–T6)
- Respuestas P0 ya cerradas:
  - Estados: DISPONIBLE / EN_CLIENTE / EN_TRANSITO / FUERA_DE_SERVICIO
  - QR generado en UI desde codigoExterno
  - EN_TRANSITO puede ser sin pedido
  - Guardar historial de cambios de estado

**Tarea:**
- Convertir T1–T6 en tickets ejecutables (1–2 días).
- Para cada ticket definir: objetivo, alcance / no alcance, criterios de aceptación, tareas técnicas, Definition of Done, labels finales.
- Marcar explícitamente dependencias y posibles "blocked".

**Salida:** Markdown listo para pegar en GitHub.

## Output (plan técnico completo)

Ver sección siguiente: plan T1–T6 en Markdown.

## Decisión humana
*(Pendiente: aceptar plan y cargar tickets en GitHub/board.)*

## Impacto en el repo
- Referencia para implementación: T1 DB → T2 Backend → T3 Frontend ABM → T4 transición estado → T5 vista estado → T6 export.

---

# Plan técnico — Inventario de Volquetes (T1–T6)

**Feature:** Inventario de Volquetes (ABM + estados + QR externo)  
**P0 cerradas:** Estados DISPONIBLE / EN_CLIENTE / EN_TRANSITO / FUERA_DE_SERVICIO; QR generado en UI desde `codigoExterno`; EN_TRANSITO puede ser sin pedido; se guarda historial de cambios de estado.

---

## T1 — DB: Tabla volquetes + estados + historial

**Objetivo:** Tener en PostgreSQL el modelo de datos de volquetes (código interno, código externo, estado actual) y el historial de cambios de estado, listo para ABM y para transiciones.

**Alcance**
- Tabla `volquetes`: id, codigo_interno (único), codigo_externo (único), estado_actual (DISPONIBLE | EN_CLIENTE | EN_TRANSITO | FUERA_DE_SERVICIO), timestamps si se definen.
- Tabla `volquete_estado_historial` (o equivalente): id, volquete_id, estado_desde, estado_hasta, fecha/hora, origen (manual/pedido) si aplica.
- Script incremental + rollback. Constraints e índices nombrados.

**No alcance**
- Lógica de negocio en DB (solo modelo). Generación de QR (solo persistir código). Reportes.

**Criterios de aceptación**
- Given el esquema actual, when se ejecuta el script incremental, then existen las tablas `volquetes` y `volquete_estado_historial` con columnas y constraints acordados.
- Given el script de rollback, when se ejecuta, then se revierte la creación de las tablas (o se documenta el rollback).

**Tareas técnicas**
- Definir DDL de `volquetes` y de historial de estado (PK, FKs, CHECK de estados, índices).
- Redactar script incremental y rollback; incluir Issue/Ticket ID en el nombre.
- Actualizar `memory-bank/06-data-model.md` con ambas tablas.

**Definition of Done**
- Scripts ejecutados sin error en BD de desarrollo; rollback probado o documentado; 06-data-model actualizado.

**Labels:** `tipo: chore` `área: db` `prioridad: p1`

**Dependencias:** Ninguna. **Blocked:** No.

---

## T2 — Backend: API CRUD volquetes + listado por estado

**Objetivo:** Exponer ABM de volquetes y listado (paginado) con filtro por estado, alineado con contrato en memory-bank.

**Alcance**
- Endpoints REST para recurso `volquetes`: GET (paginado + filtro estado), GET /{id}, POST, PUT, DELETE.
- DTOs request/response; validaciones (código interno y externo únicos); no exponer entidades JPA.
- Capas Controller → Service → Repository; errores estándar (404, 409, 422).

**No alcance**
- Transición de estado (T4). Generación de QR. Reportes/Excel (T6).

**Criterios de aceptación**
- Given un admin, when crea un volquete con codigo_interno y codigo_externo únicos y estado inicial, then se persiste y aparece en listados.
- Given el listado, when se filtra por estado DISPONIBLE, then solo se devuelven volquetes en ese estado.
- Given un volquete existente, when se intenta crear otro con mismo codigo_interno o codigo_externo, then la API responde 409 (o 422) con mensaje claro.
- Given un volquete inexistente, when GET/PUT/DELETE por ese ID, then 404.

**Tareas técnicas**
- Entidad JPA Volquete y enum/dominio de estado; repository con métodos por estado.
- Service con reglas de negocio (unicidad, validaciones); Controller con endpoints.
- Actualizar `memory-bank/04-api-documentation.md` (sección Volquetes). Tests unitarios Service + integración Controller.

**Definition of Done**
- CRUD y listado con filtro estado funcionando; validaciones y 404/409/422 probados; tests pasando; API documentada.

**Labels:** `tipo: feature` `área: backend` `prioridad: p1`

**Dependencias:** T1. **Blocked:** Si T1 no está hecho.

---

## T3 — Frontend: ABM Volquetes (listado + alta/edición/baja)

**Objetivo:** Pantalla de administración de volquetes (listado, alta, edición, baja) consumiendo la API de T2, con generación de QR en UI desde `codigoExterno`.

**Alcance**
- Vista ABM bajo layout existente (Index): listado con código interno, código externo, estado, acciones.
- Modal o página de alta/edición: código interno, código externo, estado inicial; validación UI; feedback de errores (ej. 409).
- Generación de QR en UI a partir de `codigoExterno` (mostrar o descargar; librería existente o estándar del proyecto).
- Estados loading, empty, error con retry; confirmación antes de borrar; acceso según permisos (ej. admin).

**No alcance**
- Cambio de estado desde esta pantalla (T5/T4). Reportes/Excel (T6). Impresión de etiquetas físicas (solo QR en pantalla/descarga).

**Criterios de aceptación**
- Given un admin, when entra a la vista de volquetes, then ve el listado (o empty) y puede abrir el formulario de alta.
- Given el formulario de alta, when completa código interno, código externo y estado inicial y envía, then se crea el volquete y se muestra en el listado con feedback de éxito.
- Given un volquete, when edita y guarda, then se actualizan los datos en el listado.
- Given un código externo guardado, when se muestra el detalle o listado, then se puede ver/generar QR desde ese código (en UI).
- Given error 409 (código duplicado), when la respuesta llega, then se muestra mensaje claro.

**Tareas técnicas**
- Ruta/vista ABM; servicio API volquetes (clientes de T2); tipos TS alineados al contrato.
- Formulario con validación; listado con columnas y acciones; componente o utilidad para generar QR desde `codigoExterno`.
- Respetar router/auth y API client (frontend.mdc, 07). DoD: ABM operable; loading/empty/error; feedback; QR desde codigoExterno.

**Definition of Done**
- ABM operable en local contra backend T2; listado, alta, edición, baja y QR desde codigoExterno verificados; estados y feedback cubiertos.

**Labels:** `tipo: feature` `área: frontend` `prioridad: p1`

**Dependencias:** T2. **Blocked:** Si T2 no está disponible.

---

## T4 — Backend: Transición de estado + historial

**Objetivo:** Permitir cambiar el estado del volquete (DISPONIBLE, EN_CLIENTE, EN_TRANSITO, FUERA_DE_SERVICIO) y persistir cada cambio en historial. EN_TRANSITO puede ser sin pedido.

**Alcance**
- Endpoint(s) para transición de estado (ej. PATCH /api/volquetes/{id}/estado o PUT con cuerpo de estado).
- Reglas de negocio: estados permitidos, transiciones válidas; origen opcional (manual / pedido).
- Persistir en `volquete_estado_historial` en cada cambio; actualizar `estado_actual` en `volquetes`.
- Respuestas 404, 422 para transición inválida.

**No alcance**
- Flujo completo de pedidos (solo endpoint de transición; pedidos pueden llamarlo luego). Reportes (T6).

**Criterios de aceptación**
- Given un volquete en DISPONIBLE, when se envía transición a EN_CLIENTE (o EN_TRANSITO), then el estado se actualiza y se inserta un registro en historial.
- Given un volquete en cualquier estado, when se envía transición a FUERA_DE_SERVICIO, then se actualiza estado y se registra en historial.
- Given un volquete, when se envía transición a EN_TRANSITO sin pedido, then se acepta y se guarda en historial (origen manual).
- Given una transición no permitida (ej. regla de negocio), when se intenta el cambio, then 422 con mensaje claro.
- Given volquete inexistente, when se intenta cambiar estado, then 404.

**Tareas técnicas**
- Service de transición de estado; validación de transiciones; escritura en tabla de historial y actualización de `volquetes`.
- Documentar contrato en 04-api-documentation. Tests unitarios (transiciones válidas e inválidas) y de integración.

**Definition of Done**
- Transición de estado y persistencia de historial funcionando; reglas validadas; tests pasando; API documentada.

**Labels:** `tipo: feature` `área: backend` `prioridad: p1`

**Dependencias:** T2 (y modelo T1 con tabla historial). **Blocked:** Si T1/T2 no están hechos.

---

## T5 — Frontend: Vista estado inventario + filtros por estado

**Objetivo:** Ver el estado actual de cada volquete y filtrar por estado (DISPONIBLE, EN_CLIENTE, EN_TRANSITO, FUERA_DE_SERVICIO), con actualización vía recarga o polling (v1).

**Alcance**
- Vista o sección "Estado del inventario": listado/cards de volquetes con estado actual.
- Filtros por estado (combo o chips); botón "Actualizar" o polling cada X segundos.
- Estados loading y error; consumo del listado con filtro estado (API T2/T4).

**No alcance**
- WebSockets/push en v1 (solo polling o recarga manual). Reportes/Excel (T6). Cambio de estado desde esta vista (opcional; si se agrega, dependería de T4).

**Criterios de aceptación**
- Given un usuario con permiso, when abre la vista de estado de inventario, then ve los volquetes con su estado actual.
- When selecciona filtro por estado (ej. EN_CLIENTE), then solo se muestran volquetes en ese estado.
- When actualiza (botón o polling), then ve datos actualizados desde el backend.

**Tareas técnicas**
- Vista o ruta dedicada; llamada a API listado con query param de estado; filtros en UI; polling opcional (setInterval o similar); manejo loading/error.

**Definition of Done**
- Vista operativa; filtros funcionando; datos alineados con backend; actualización manual o por polling verificada.

**Labels:** `tipo: feature` `área: frontend` `prioridad: p1`

**Dependencias:** T2. Opcionalmente T4 si se muestra último cambio o se permite cambiar estado desde esta vista. **Blocked:** Si T2 no está disponible.

---

## T6 — Reportes / export Excel inventario (v1 básico)

**Objetivo:** Ofrecer exportación básica del inventario (lista de volquetes con estado, códigos, etc.) en Excel o CSV para uso operativo o auditoría.

**Alcance**
- Endpoint de export en backend (generación de archivo Excel o CSV); o endpoint que devuelva datos y el frontend arme el archivo (según estándar del proyecto).
- En frontend: botón o enlace "Exportar inventario" que dispare la descarga.
- Contenido: columnas alineadas al listado (código interno, código externo, estado, etc.); sin inventar campos.

**No alcance**
- Reportes analíticos complejos; múltiples formatos o plantillas en v1.

**Criterios de aceptación**
- Given un usuario autorizado, when solicita export de inventario, then recibe un archivo (Excel o CSV) con la lista de volquetes y sus estados/códigos.
- When no hay volquetes, then recibe archivo vacío o con cabeceras y sin filas (o mensaje coherente).

**Tareas técnicas**
- Backend: endpoint GET (ej. /api/volquetes/export) que genere el archivo o devuelva datos listos para export; librería estándar (ej. para Excel) si ya existe en el proyecto.
- Frontend: llamada al endpoint y descarga del archivo; botón en vista de inventario o ABM.
- Documentar en 04 si se agrega endpoint nuevo.

**Definition of Done**
- Export ejecutado desde UI; archivo descargado con datos correctos; sin errores con lista vacía.

**Labels:** `tipo: feature` `área: backend` `área: frontend` `prioridad: p2`

**Dependencias:** T2, T3 (o al menos listado disponible). **Blocked:** Hasta tener listado de volquetes (T2) y opcionalmente vista donde poner el botón (T3/T5).

---

## Resumen de dependencias y blocked

| Ticket | Dependencias | Blocked si |
|--------|--------------|------------|
| T1 | — | — |
| T2 | T1 | Falta tabla volquetes |
| T3 | T2 | Falta API CRUD volquetes |
| T4 | T1, T2 | Falta modelo + API base |
| T5 | T2 | Falta API listado con filtro |
| T6 | T2 (y recomendable T3) | Falta listado/API |

**Orden sugerido de implementación:** T1 → T2 → T3 (ABM completo); en paralelo o después T4; luego T5; por último T6 cuando se priorice reportes.
