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
- Errores: 401/403/404/409 (409 si tiene pedidos asociados).

---

### Pedidos

Recurso: `/api/pedidos`. Creación y gestión de pedidos con cliente, dirección de entrega, volquete obligatorio y estados (NUEVO → ASIGNADO → ENTREGADO → RETIRADO → CERRADO; CANCELADO desde NUEVO/ASIGNADO). Modalidad de precio (común/abono) se deriva del cliente, no se persiste en pedidos. Sin DELETE físico; solo cancelación (estado CANCELADO).

#### GET /api/pedidos
- Descripción: Listado paginado con filtros por estado, cliente y volquete.
- Permiso requerido: Admin o Chofer.
- Query params: `page`, `size`, `sort` (Pageable); `estado` (opcional): NUEVO | ASIGNADO | ENTREGADO | RETIRADO | CERRADO | CANCELADO; `clienteId` (opcional); `volqueteId` (opcional).
- Response 200: Page&lt;PedidoResponse&gt; (content, totalElements, totalPages, size, number).
- Errores: 400 (params inválidos), 401/403

#### GET /api/pedidos/{id}
- Descripción: Detalle de un pedido (incluye datos de cliente, volquete, chofer/camión si aplica).
- Permiso requerido: Admin o Chofer.
- Response 200: PedidoResponse (id, clienteId, volqueteId, direccionEntrega, estado, fechaCreacion, fechaEntregaPrevista, fechaEntregaReal, fechaRetiroReal, choferId, camionId; opcionalmente clienteNombre, volqueteCodigoInterno, choferNombre, camionPatente).
- Errores: 401/403/404

#### POST /api/pedidos
- Descripción: Crear pedido (estado inicial NUEVO). Cliente y volquete obligatorios; volquete no puede estar en otro pedido activo.
- Permiso requerido: Admin.
- Request: PedidoRequest — `clienteId` (Long), `volqueteId` (Long), `direccionEntrega` (String), `fechaEntregaPrevista` (ISO-8601, opcional).
- Response 201: PedidoResponse (Location: /api/pedidos/{id}).
- Errores: 400 (JSON/validación), 401/403/404 (cliente o volquete inexistente), 409 (volquete ya en pedido activo), 422 (cliente/volquete faltante u otra regla).

#### PUT /api/pedidos/{id}
- Descripción: Actualizar pedido (campos editables: clienteId, volqueteId, direccionEntrega, fechaEntregaPrevista). Al cambiar volquete, el nuevo no debe estar en otro pedido activo.
- Permiso requerido: Admin.
- Request: PedidoRequest (mismos campos que POST para los editables).
- Response 200: PedidoResponse.
- Errores: 400/401/403/404/409/422 (409 si el nuevo volquete ya está en otro pedido activo).

#### PATCH /api/pedidos/{id}/estado
- Descripción: Cambiar estado del pedido. Para destino ASIGNADO son obligatorios choferId, camionId, fechaEntregaPrevista. En ENTREGADO/RETIRADO/CANCELADO se actualiza estado del volquete e historial (origen PEDIDO).
- Permiso requerido: Admin o Chofer.
- Request: PedidoEstadoRequest — `estado` (obligatorio); para ASIGNADO: `choferId`, `camionId`, `fechaEntregaPrevista` (obligatorios).
- Response 200: PedidoResponse (estado y datos actualizados).
- Errores: 400 (estado inválido), 401/403/404/422 (transición no permitida, datos faltantes para ASIGNADO, pedido ya CERRADO/CANCELADO).

**DTOs Pedidos:** PedidoRequest (clienteId, volqueteId, direccionEntrega, fechaEntregaPrevista opcional). PedidoResponse (id, clienteId, volqueteId, direccionEntrega, estado, fechaCreacion, fechaEntregaPrevista, fechaEntregaReal, fechaRetiroReal, choferId, camionId; opcionalmente datos anidados para detalle). PedidoEstadoRequest (estado; choferId, camionId, fechaEntregaPrevista para ASIGNADO). Listado: Page&lt;PedidoResponse&gt;.

**Transiciones permitidas:** NUEVO → ASIGNADO (con chofer/camión/fecha) o CANCELADO; ASIGNADO → ENTREGADO o CANCELADO; ENTREGADO → RETIRADO; RETIRADO → CERRADO. CERRADO y CANCELADO no admiten más transiciones.

---

### Choferes (selector)

#### GET /api/choferes/selector
- Descripción: Lista liviana para combo al asignar pedido (id, nombre).
- Permiso requerido: Admin o Chofer.
- Response 200: `[ { "id": 1, "nombre": "string" } ]`
- Errores: 401/403

---

### Camiones (selector)

#### GET /api/camiones/selector
- Descripción: Lista liviana para combo al asignar pedido (id, patente).
- Permiso requerido: Admin o Chofer.
- Response 200: `[ { "id": 1, "patente": "string" } ]`
- Errores: 401/403

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
- Descripción: Eliminar volquete (hard delete). 409 si existe un pedido activo (estado NUEVO, ASIGNADO o ENTREGADO) con ese volquete.
- Permiso requerido: admin.
- Response 204: sin cuerpo.
- Errores: 401/403/404/409 (409 si tiene pedido activo asociado)

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
- Errores: 400 (estado/origen inválido), 401/403/404/422 (422 reservado para futuras reglas de negocio que restrinjan transiciones).
- **Reglas de transición (v1):** Se permiten todas las transiciones entre los 4 estados. Cambiar al mismo estado es válido y se registra en historial (permite re-registrar con diferente origen si aplica). EN_TRANSITO puede ser sin pedido (origen MANUAL). El código 422 está reservado para futuras reglas de negocio que puedan restringir transiciones específicas.

#### GET /api/volquetes/export
- Descripción: Exportar inventario completo en formato CSV. Obtiene todos los volquetes (sin paginación) y genera archivo CSV con código interno, código externo y estado actual.
- Permiso requerido: admin (mismo criterio que otros endpoints de volquetes).
- Query params: ninguno.
- Response 200: archivo CSV con Content-Type `text/csv; charset=UTF-8` y Content-Disposition `attachment; filename="inventario-volquetes.csv"`.
- Formato CSV: cabeceras "Código Interno,Código Externo,Estado Actual"; una fila por volquete. Si no hay volquetes, devuelve solo las cabeceras.
- Errores: 401/403/500

**DTOs Volquetes:** VolqueteRequest (codigoInterno, codigoExterno; POST además estadoInicial opcional). VolqueteResponse (id, codigoInterno, codigoExterno, estadoActual). VolqueteEstadoRequest (estado, origen opcional). Listado: Page&lt;VolqueteResponse&gt; (content, totalElements, totalPages, size, number).

**Códigos de error esperados:** 400 (params/JSON/estado inválido), 401, 403, 404 (recurso no encontrado), 409 (código duplicado), 422 (validación/transición), 500.
