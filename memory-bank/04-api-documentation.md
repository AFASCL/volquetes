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
