# Arquitectura — Inventario de Volquetes (T1 + T2)

## Herramienta consultada
Cursor — Agente 4 (Architect), según AGENTS.md.

## Input (prompt)

Actuá como AGENTE 4 — Architect.

Feature: Inventario de Volquetes (ABM + estados + QR externo).

Fuente de verdad obligatoria: memory-bank/00-project-overview.md, 01-architecture.md, 04-api-documentation.md, 06-data-model.md, general.mdc, AGENTS.md.

Contexto ya definido (NO volver a preguntar):
- Estados: DISPONIBLE / EN_CLIENTE / EN_TRANSITO / FUERA_DE_SERVICIO
- El QR se genera en la UI a partir de codigoExterno
- EN_TRANSITO puede existir sin pedido asignado
- Se requiere historial de cambios de estado
- No inventar workflows ni permisos nuevos

Objetivo: Diseñar la arquitectura técnica de T1 y T2 del Inventario de Volquetes.

Entregables obligatorios:
1) Modelo de datos: tabla volquetes, tabla historial de estados, campos, PK, FK, constraints, índices.
2) Contrato API: endpoints CRUD volquetes, endpoint(s) de transición de estado, DTOs request/response, códigos de error esperados.
3) Decisiones arquitectónicas (ADR corto): Enum vs VARCHAR, historial separado, estado actual vs derivado.
4) Qué archivos del memory-bank deben actualizarse y en qué sección.

Restricciones: No escribir código; no crear issues; no actualizar archivos automáticamente; SOLO diseño técnico y contratos. Formato: Markdown, secciones claras, listo para Builder Backend y Frontend.

## Output (resumen y extractos)

La IA generó el documento **docs/arch-inventario-volquetes-t1-t2.md** con:

**1) Modelo de datos**
- **volquetes:** id (PK), codigo_interno / codigo_externo (VARCHAR UNIQUE), estado_actual (CHECK: DISPONIBLE|EN_CLIENTE|EN_TRANSITO|FUERA_DE_SERVICIO), created_at/updated_at opcionales. Índice por estado_actual.
- **volquete_estado_historial:** id (PK), volquete_id (FK), estado_desde, estado_hasta, fecha_hora, origen (MANUAL|PEDIDO opcional). CHECK para estados y origen. Índice por volquete_id. Scripts incremental y rollback con convención YYYYMMDD_issueId_volquetes.

**2) Contrato API**
- CRUD: GET /api/volquetes (paginado + query `estado`), GET /api/volquetes/{id}, POST, PUT, DELETE. DTOs: VolqueteRequest (codigoInterno, codigoExterno, estadoInicial opcional), VolqueteResponse (id, codigoInterno, codigoExterno, estadoActual).
- Transición (T4): PATCH /api/volquetes/{id}/estado con { estado, origen? }; 200 con body de volquete; 404/422. Errores: 400, 401, 403, 404, 409 (código duplicado), 422, 500.

**3) ADRs**
- Enum vs VARCHAR: VARCHAR + CHECK en BD; enums en Java (alineado a clientes).
- Historial en tabla separada (volquete_estado_historial) para auditoría y consultas.
- Estado actual denormalizado en volquetes.estado_actual; en cada transición se actualiza y se inserta en historial en la misma transacción.

**4) Memory-bank a actualizar**
- 06-data-model.md: añadir subsecciones volquetes y volquete_estado_historial.
- 04-api-documentation.md: sección Volquetes con todos los endpoints y DTOs.
- 01-architecture.md: registrar las tres ADRs en "4) Decisiones / ADRs".

## Decisión humana
(Pendiente: aceptar diseño, ejecutar actualizaciones al memory-bank cuando se implemente T1/T2.)

## Impacto en el repo
- **Creado:** docs/arch-inventario-volquetes-t1-t2.md (diseño de referencia).
- **Por actualizar** (cuando se implemente): memory-bank/06-data-model.md, memory-bank/04-api-documentation.md, memory-bank/01-architecture.md (según secciones indicadas en el doc).
