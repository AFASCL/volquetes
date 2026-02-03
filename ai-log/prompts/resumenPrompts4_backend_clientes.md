# T2 — API CRUD Clientes + selector — Salida Builder Backend

## 0) Supuestos + preguntas bloqueantes

- **Supuesto:** Mismo DTO para crear y actualizar (`ClienteRequest`), según contrato 04 (request igual para POST y PUT).
- **Supuesto:** Selector sin paginación ni límite en v1; listado completo (opcional: límite en memoria-bank si se define después).
- **Supuesto:** Seguridad no existe; se dejó "PENDIENTE" en `memory-bank/01-architecture.md`; no se inventó auth.
- **Preguntas bloqueantes:** Ninguna.

---

## A) Resumen

1. **Entidad** `Cliente` mapeada a tabla `clientes` (id, razon_social, telefono, email, direccion_principal, tipo); enum `ClienteTipo` (COMUN, ABONO) persistido como String.
2. **Repository** `ClienteRepository` con JpaRepository + `findSelectorItems()` con proyección (id, razonSocial, tipo) sin N+1.
3. **DTOs:** `ClienteRequest` (create/update), `ClienteResponse`, `ClienteSelectorItemResponse`; `ErrorResponse` para formato estándar de error.
4. **Service** `ClienteService`: CRUD + selector; validación negocio `razonSocial` no vacío → 422; `ClienteNotFoundException` → 404.
5. **Controller** `ClienteController`: GET /api/clientes (Pageable), GET /api/clientes/selector, GET /api/clientes/{id}, POST, PUT, DELETE.
6. **GlobalExceptionHandler:** formato estándar (code, message, details, timestamp); 404, 422 (Bean Validation + ClienteValidationException), 400 (JSON inválido), 500.
7. **Tests:** `ClienteServiceTest` (create ok, create nombre vacío → 422, get no encontrado → 404); `ClienteControllerTest` (POST 201, GET selector 200).
8. **memory-bank:** Seguridad marcada PENDIENTE en 01-architecture. 04 y 06 no requirieron cambios (contrato y modelo ya alineados).

---

## B) Archivos creados / modificados

| Acción | Archivo |
|--------|---------|
| Crear | `backend/src/main/java/com/afascl/volquetes/domain/ClienteTipo.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/domain/Cliente.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/domain/ClienteNotFoundException.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/domain/ClienteValidationException.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/dto/ClienteRequest.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/dto/ClienteResponse.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/dto/ClienteSelectorItemResponse.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/dto/ErrorResponse.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/repository/ClienteRepository.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/service/ClienteService.java` |
| Crear | `backend/src/main/java/com/afascl/volquetes/controller/ClienteController.java` |
| Modificar | `backend/src/main/java/com/afascl/volquetes/controller/advice/GlobalExceptionHandler.java` |
| Modificar | `memory-bank/01-architecture.md` (seguridad PENDIENTE) |
| Crear | `backend/src/test/java/com/afascl/volquetes/service/ClienteServiceTest.java` |
| Crear | `backend/src/test/java/com/afascl/volquetes/controller/ClienteControllerTest.java` |

---

## C) Código por archivo (referencia)

El código está implementado en el repo. Resumen de responsabilidades:

- **Cliente / ClienteTipo:** JPA entity y enum; columnas según 06-data-model (razon_social, direccion_principal, tipo COMUN|ABONO).
- **ClienteRequest:** Bean Validation @NotBlank razonSocial, @NotNull tipo, @Email opcional, @Size según contrato.
- **ClienteResponse / ClienteSelectorItemResponse:** Campos según 04-api-documentation.
- **ClienteRepository:** `findAll(Pageable)` heredado; `findSelectorItems()` con `@Query` y proyección interface (id, razonSocial, tipo).
- **ClienteService:** @Transactional readOnly en lecturas; validación `razonSocial` no blank en create/update; lanza `ClienteNotFoundException` y `ClienteValidationException`.
- **ClienteController:** Rutas bajo `/api/clientes`; GET /selector declarado antes de GET /{id}; POST devuelve 201 + Location.
- **GlobalExceptionHandler:** ErrorResponse (code, message, details, timestamp); handlers para NotFound, Validation, MethodArgumentNotValidException (422), HttpMessageNotReadableException (400), Exception (500).

---

## D) Tests

- **ClienteServiceTest:** create ok (verifica response y save), create razonSocial blank → ClienteValidationException, findById no encontrado → ClienteNotFoundException.
- **ClienteControllerTest (@WebMvcTest):** POST /api/clientes con body válido → 201 y cuerpo con id, razonSocial, tipo; GET /api/clientes/selector → 200 y array con item (id, razonSocial, tipo).

Ejecutar: `mvn -f backend test "-Dtest=ClienteServiceTest,ClienteControllerTest"`

---

## E) Cómo probar localmente

1. Tener PostgreSQL 16+ con base creada y script `db/scripts/20260201_clientes.sql` ejecutado.
2. Variables de entorno (opcional): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
3. `mvn -f backend spring-boot:run`.
4. Probar:
   - `GET http://localhost:8080/api/health` → 200, `{"status":"UP"}`.
   - `GET http://localhost:8080/api/clientes?page=0&size=20` → 200, página vacía o con datos.
   - `GET http://localhost:8080/api/clientes/selector` → 200, `[]` o items.
   - `POST http://localhost:8080/api/clientes` con body `{"razonSocial":"Cliente SA","tipo":"COMUN"}` → 201, Location y cuerpo.
   - `GET http://localhost:8080/api/clientes/1` → 200 o 404.
   - `PUT http://localhost:8080/api/clientes/1` con mismo body → 200 o 404.
   - `DELETE http://localhost:8080/api/clientes/1` → 204 o 404.
   - POST con `razonSocial: ""` o sin `tipo` → 422 con formato estándar de error.

---

## F) Riesgos / pendientes

- **Seguridad:** Pendiente; endpoints abiertos. Cuando se defina auth, restringir según 04 (admin para ABM).
- **DELETE 409:** Contrato indica 409 si tiene pedidos asociados; al no existir aún tabla pedidos, no se implementó; agregar cuando exista FK pedidos → clientes.
- **Orden de rutas:** GET /selector está antes de GET /{id} para que "selector" no se interprete como id; mantener al agregar más rutas.
