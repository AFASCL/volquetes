# Arquitectura — Clientes (PRD v1)

## Herramienta consultada
Cursor 2.4 — Agente 4 (Architect), según AGENTS.md.

## Input (prompt)

Actuá como Agente 4 — Architect. Definí el diseño técnico para el recurso Clientes según el Issue:

- Modelo de datos PostgreSQL 16+ (tabla clientes + constraints + índices).
- Script SQL incremental + rollback manual (solo especificación, no código aún).
- Contrato API REST (endpoints, DTOs request/response, errores).
- Reglas de negocio y validaciones (qué va en DTO vs Service).
- Seguridad mínima (admin vs chofer).
- Actualizaciones a memory-bank: 04-api-documentation.md (sección Clientes), 06-data-model.md (tabla clientes).

No inventes campos fuera de lo definido: nombre/razón social, teléfono, email, dirección principal, tipo (común/abono). Código interno/externo NO aplica. Si falta algo, proponé como "opcional" y marcá supuesto.

**Issue:** Objetivo: gestionar clientes para asociarlos a pedidos (común/abono). Alcance: ABM, tipo común/abono, datos mínimos. Criterios: admin crea cliente → listados y selector en pedido; cliente abono → modalidad abono en pedidos; validación nombre no vacío. Impacto: Backend CRUD + DTOs; Frontend ABM. Subtareas T1–T4 y dependencias.

## Output (resumen y extractos)

La IA generó:

**1) Contrato API — Clientes**
- Recurso `/api/clientes`. GET listado paginado (content, totalElements, totalPages, size, number); GET /selector (array id, razonSocial, tipo); GET /{id}; POST (body con razonSocial, telefono, email, direccionPrincipal, tipo); PUT /{id}; DELETE /{id} (204). Códigos 400/401/403/404/422/409.

**2) Modelo de datos**
- Tabla `clientes`: id (BIGINT identity), razon_social VARCHAR(255) NOT NULL, telefono, email, direccion_principal, tipo VARCHAR(10) NOT NULL CHECK (COMUN|ABONO). Índices: idx_clientes_razon_social, idx_clientes_tipo. Scripts incremental y rollback con convención YYYYMMDD_issueId_descripcion.

**3) Reglas**
- Validación nombre no vacío en DTO y Service. Errores estándar (code, message, details, timestamp). Seguridad: admin para ABM (pendiente implementación).

Luego se actualizaron 04 y 06 con la sección Clientes; después se alineó el campo a `nombre` (razon_social → nombre en DB y API).

## Decisión humana
- Aceptar un único campo `nombre` (nombre/razón social) en v1.
- Tipo VARCHAR + CHECK (no enum nativo PostgreSQL).
- Sin unicidad de nombre en v1; eliminación física; endpoint `/selector` separado.

## Impacto en el repo
- memory-bank/04-api-documentation.md (sección Clientes).
- memory-bank/06-data-model.md (tabla clientes).
