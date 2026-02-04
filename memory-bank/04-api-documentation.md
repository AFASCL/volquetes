# 04 — API Documentation (Contrato de API)

> Objetivo: que Frontend/Backend compartan un contrato claro.
> Mantener ejemplos breves. No inventar endpoints.

## 1) Convenciones
- Base URL: `<BASE_URL>`
- Content-Type: `application/json`
- Auth: `Authorization: Bearer <token>` (si aplica)

## 2) Formato estándar de error
```json
{
  "code": "STRING",
  "message": "STRING",
  "details": [],
  "timestamp": "2026-01-11T12:00:00Z"
}
```

## 3) Endpoints

### Clientes

Recurso: `/api/clientes` (plural). Solo campos definidos: nombre/razón social, teléfono, email, dirección principal, tipo (común/abono). Código interno/externo no aplica.

#### GET /api/clientes
- Descripción: Listado paginado de clientes (ABM).
- Permiso requerido: admin (chofer solo lectura si se define más adelante; supuesto: admin para ABM).
- Query params: `page`, `size`, `sort` (estándar Pageable).
- Response 200:
```json
{
  "content": [
    {
      "id": 1,
      "nombre": "string",
      "telefono": "string",
      "email": "string",
      "direccionPrincipal": "string",
      "tipo": "COMUN"
    }
  ],
  "totalElements": 0,
  "totalPages": 0,
  "size": 20,
  "number": 0
}
```
- Errores: 400/401/403

#### GET /api/clientes/selector
- Descripción: Listado liviano para combo/selector al crear pedido (id, razón social, tipo).
- Permiso requerido: admin (o rol que pueda crear pedidos).
- Query params: ninguno obligatorio (opcional: límite si se define).
- Response 200:
```json
[
  { "id": 1, "nombre": "string", "tipo": "COMUN" }
]
```
- Errores: 401/403

#### GET /api/clientes/{id}
- Descripción: Detalle de un cliente por ID.
- Permiso requerido: admin.
- Response 200:
```json
{
  "id": 1,
  "razonSocial": "string",
  "telefono": "string",
  "email": "string",
  "direccionPrincipal": "string",
  "tipo": "COMUN"
}
```
- Errores: 401/403/404

#### POST /api/clientes
- Descripción: Crear cliente.
- Permiso requerido: admin.
- Request:
```json
{
  "nombre": "string",
  "telefono": "string",
  "email": "string",
  "direccionPrincipal": "string",
  "tipo": "COMUN"
}
```
- Response 201: mismo cuerpo que GET /api/clientes/{id} (Location: /api/clientes/{id}).
- Errores: 400/401/403/422 (422 si nombre vacío u otra regla de negocio).

#### PUT /api/clientes/{id}
- Descripción: Actualizar cliente.
- Permiso requerido: admin.
- Request: mismo cuerpo que POST.
- Response 200: mismo cuerpo que GET /api/clientes/{id}.
- Errores: 400/401/403/404/422

#### DELETE /api/clientes/{id}
- Descripción: Eliminar cliente (soft-delete no definido; supuesto: hard delete).
- Permiso requerido: admin.
- Response 204: sin cuerpo.
- Errores: 401/403/404/409 (409 si tiene pedidos asociados, cuando exista pedidos).

---

### Volquetes

Recurso: `/api/volquetes`. Campos: código interno, código externo, estado actual (DISPONIBLE | EN_CLIENTE | EN_TRANSITO | FUERA_DE_SERVICIO). QR se genera en UI desde codigoExterno. Permiso: admin para ABM (mismo criterio que clientes).

#### GET /api/volquetes
- Descripción: Listado paginado de volquetes con filtro opcional por estado.
- Permiso requerido: admin.
- Query params: `page`, `size`, `sort` (estándar Pageable); `estado` (opcional): DISPONIBLE | EN_CLIENTE | EN_TRANSITO | FUERA_DE_SERVICIO. Si se omite, se listan todos.
- Response 200:
```json
{
  "content": [
    {
      "id": 1,
      "codigoInterno": "string",
      "codigoExterno": "string",
      "estadoActual": "DISPONIBLE"
    }
  ],
  "totalElements": 0,
  "totalPages": 0,
  "size": 20,
  "number": 0
}
```
- Errores: 400 (estado no permitido u otros params inválidos), 401/403

#### GET /api/volquetes/{id}
- Descripción: Detalle de un volquete por ID.
- Permiso requerido: admin.
- Response 200:
```json
{
  "id": 1,
  "codigoInterno": "string",
  "codigoExterno": "string",
  "estadoActual": "DISPONIBLE"
}
```
- Errores: 401/403/404

#### POST /api/volquetes
- Descripción: Crear volquete. Estado inicial en cuerpo; si se omite, DISPONIBLE.
- Permiso requerido: admin.
- Request:
```json
{
  "codigoInterno": "string",
  "codigoExterno": "string",
  "estadoInicial": "DISPONIBLE"
}
```
- codigoInterno, codigoExterno: obligatorios, no vacíos. estadoInicial: opcional (default DISPONIBLE).
- Response 201: mismo cuerpo que GET /api/volquetes/{id} (Location: /api/volquetes/{id}).
- Errores: 400/401/403/409/422 (409 código duplicado; 422 validación negocio)

#### PUT /api/volquetes/{id}
- Descripción: Actualizar volquete. Solo codigoInterno y codigoExterno (no estado; usar PATCH estado).
- Permiso requerido: admin.
- Request: codigoInterno, codigoExterno (estadoInicial se ignora en PUT).
- Response 200: mismo cuerpo que GET /api/volquetes/{id}.
- Errores: 400/401/403/404/409/422

#### DELETE /api/volquetes/{id}
- Descripción: Eliminar volquete (hard delete). 409 si en el futuro hubiera pedidos asociados.
- Permiso requerido: admin.
- Response 204: sin cuerpo.
- Errores: 401/403/404 (y 409 cuando se defina restricción por pedidos)

#### PATCH /api/volquetes/{id}/estado
- Descripción: Cambiar estado actual. Actualiza volquetes.estado_actual e inserta en volquete_estado_historial.
- Permiso requerido: admin.
- Request:
```json
{
  "estado": "EN_CLIENTE",
  "origen": "MANUAL"
}
```
- estado: obligatorio (DISPONIBLE | EN_CLIENTE | EN_TRANSITO | FUERA_DE_SERVICIO). origen: opcional (MANUAL | PEDIDO); si se omite, NULL en historial.
- Response 200: mismo cuerpo que GET /api/volquetes/{id} (estado actualizado).
- Errores: 400 (estado/origen inválido), 401/403/404/422 (422 transición no permitida por reglas de negocio)

**DTOs Volquetes:** VolqueteRequest (codigoInterno, codigoExterno; POST además estadoInicial opcional). VolqueteResponse (id, codigoInterno, codigoExterno, estadoActual). VolqueteEstadoRequest (estado, origen opcional). Listado: Page&lt;VolqueteResponse&gt; (content, totalElements, totalPages, size, number).

**Códigos de error esperados:** 400 (params/JSON/estado inválido), 401, 403, 404 (recurso no encontrado), 409 (código duplicado), 422 (validación/transición), 500.
