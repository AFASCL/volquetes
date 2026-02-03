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

### <Nombre del recurso>
#### GET /api/<recurso>
- Descripción:
- Permiso requerido:
- Query params:
- Response 200:
```json
{ }
```
- Errores: 400/401/403/404

#### POST /api/<recurso>
- Descripción:
- Permiso requerido:
- Request:
```json
{ }
```
- Response 201:
```json
{ }
```
- Errores: 400/401/403/409/422
