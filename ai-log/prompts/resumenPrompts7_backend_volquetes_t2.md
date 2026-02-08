# T2 — Backend: API CRUD volquetes + listado por estado (Builder Backend)

## Herramienta consultada
Cursor — Agente 5 (Builder Backend), según AGENTS.md.

## Input (prompt)

Actuá como **Agente 5 — Builder Backend (Spring Boot Java)**.

Fuente de verdad obligatoria: memory-bank/01-architecture.md, 04-api-documentation.md, 06-data-model.md, backend.mdc + general.mdc.

Objetivo: Implementar el ticket **T2 Backend: API CRUD volquetes + listado por estado**.

Contexto/alcance (Tech Plan + Architect): Exponer ABM de volquetes y listado (paginado) con filtro por estado. Endpoints REST: GET (paginado + filtro estado), GET /{id}, POST, PUT, DELETE. DTOs request/response; validaciones (código interno y externo únicos); no exponer entidades JPA. Capas Controller → Service → Repository; errores estándar (404, 409, 422). No alcance: transición de estado (T4), QR, reportes.

Criterios de aceptación: admin crea volquete con códigos únicos y estado inicial → persiste y aparece en listados; listado filtrable por estado DISPONIBLE; duplicado codigo_interno/codigo_externo → 409 (o 422) con mensaje claro; GET/PUT/DELETE por id inexistente → 404.

Tareas técnicas: Entidad JPA Volquete y enum estado; repository con métodos por estado; Service con reglas de negocio (unicidad, validaciones); Controller con endpoints; actualizar memory-bank 04 si aplica; tests unitarios Service + integración Controller.

Repo: backend/, puerto 8080, Maven. Restricciones: NO inventar endpoints/campos/reglas; no exponer entidades JPA; Controller→Service→Repository; Bean Validation + negocio en Service; errores 400/404/409/422/500 con @ControllerAdvice; sin librerías nuevas.

Entregables: cambios de código (archivos completos), tests mínimos, SQL si aplica, actualización memory-bank solo en secciones afectadas. Salida en orden: Supuestos, Resumen, Archivos, Código, SQL, Tests, Pasos probar local, Memory-bank, Riesgos.

## Output (resumen y extractos)

La IA implementó en el repo:

- **Domain:** VolqueteEstado (enum), Volquete (entity con codigo_interno, codigo_externo, estado_actual, createdAt/updatedAt; @PrePersist/@PreUpdate). Excepciones: VolqueteNotFoundException (404), VolqueteValidationException (422), VolqueteConflictException (409), BadRequestException (400).
- **DTOs:** VolqueteRequest (codigoInterno, codigoExterno, estadoInicial opcional), VolqueteResponse (id, codigoInterno, codigoExterno, estadoActual).
- **Repository:** VolqueteRepository con findAll(Pageable), findByEstadoActual(estado, Pageable), existsByCodigoInterno/Externo y variantes AndIdNot para unicidad.
- **Service:** VolqueteService con CRUD, validación códigos no vacíos, chequeo unicidad en create/update, parseEstadoFilter(estado) para query param (lanza BadRequestException si valor inválido).
- **Controller:** GET /api/volquetes (page, size, estado opcional), GET /{id}, POST, PUT, DELETE.
- **GlobalExceptionHandler:** handlers para BadRequestException (400), VolqueteNotFoundException (404), VolqueteValidationException (422), VolqueteConflictException (409).
- **Tests:** VolqueteServiceTest (create ok, blank 422, duplicate 409, findById 404, parseEstadoFilter); VolqueteControllerTest (list 200, byId 200, POST 201, PUT 200, DELETE 204, byId 404 con body NOT_FOUND).

No se modificó memory-bank (contrato y modelo ya documentados en diseño Architect). No SQL en T2 (T1 ya entregó scripts).

## Decisión humana
(Pendiente: aceptar implementación y marcar T2 como cerrado en el board.)

## Impacto en el repo
- Backend: domain (Volquete, VolqueteEstado, excepciones), dto (VolqueteRequest, VolqueteResponse), repository, service, controller; GlobalExceptionHandler ampliado; tests VolqueteServiceTest, VolqueteControllerTest.
