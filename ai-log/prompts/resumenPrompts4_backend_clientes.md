# T2 — API CRUD Clientes + selector (Builder Backend)

## Herramienta consultada
Cursor 2.4 — Agente 5 (Builder Backend), según AGENTS.md.

## Input (prompt)

Actuá como Agente 5 — Builder Backend. Implementá T2: API CRUD de Clientes + endpoint selector, siguiendo estrictamente memory-bank/04-api-documentation.md, 06-data-model.md, y reglas de backend.mdc (NO inventar).

**Alcance:** Persistencia (entidad Cliente, enum ClienteTipo, repository con CRUD y método selector sin N+1). API: GET /api/clientes (Pageable), GET /api/clientes/selector, GET /api/clientes/{id}, POST, PUT, DELETE. DTOs ClienteRequest, ClienteResponse, ClienteSelectorItemResponse. Validaciones: nombre @NotBlank, tipo obligatorio, email @Email si viene. No exponer entidad JPA. Errores: @ControllerAdvice con formato estándar (code, message, details, timestamp); 404, 422, 400, 409 reservado. Seguridad: no inventar; si no existe, dejar PENDIENTE en memory-bank. Tests mínimos: Service (create ok, nombre vacío 422, get no encontrado 404); Controller MockMvc (POST 201, GET selector 200). Salida en Markdown: supuestos, resumen, archivos, código, tests, cómo probar, riesgos.

## Output (resumen y extractos)

La IA entregó un documento Markdown con:

- **Supuestos:** Mismo DTO create/update; selector sin paginación en v1; seguridad PENDIENTE.
- **Resumen:** Entidad Cliente (id, razon_social, telefono, email, direccion_principal, tipo); ClienteRepository con findSelectorItems() y proyección; ClienteService con CRUD, validateRazonSocialNotBlank, ClienteNotFoundException/ClienteValidationException; ClienteController con todas las rutas; GlobalExceptionHandler con ErrorResponse y handlers 404/422/400/500; tests unitarios y de integración.
- **Archivos creados:** domain (Cliente, ClienteTipo, ClienteNotFoundException, ClienteValidationException), dto (ClienteRequest, ClienteResponse, ClienteSelectorItemResponse, ErrorResponse), repository, service, controller, controller/advice; tests ClienteServiceTest, ClienteControllerTest. Modificados: GlobalExceptionHandler, 01-architecture (seguridad PENDIENTE).
- **Código:** Implementación completa en el repo (entidades, DTOs, repository con @Query y proyección, service con transacciones y validación, controller, manejo de errores).
- **Tests:** create ok, create nombre vacío → 422, findById no encontrado → 404; POST 201, GET selector 200.
- **Riesgos/pendientes:** Seguridad pendiente; DELETE 409 cuando exista tabla pedidos.

Posteriormente se alineó el campo a `nombre` (razon_social → nombre en entidad, DTOs, API y memory-bank).

## Decisión humana
Aceptar implementación; luego alineación razonSocial → nombre en DB (script rename), backend (entity/DTOs/service/repo) y memory-bank.

## Impacto en el repo
- Backend: domain, dto, repository, service, controller, advice; tests.
- memory-bank/01-architecture.md (seguridad PENDIENTE).
