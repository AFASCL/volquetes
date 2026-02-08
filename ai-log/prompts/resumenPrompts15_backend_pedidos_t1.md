# Pedidos — Backend T1 (DB: tablas pedidos, choferes, camiones)

## Herramienta consultada
Cursor (Agente 5 — Builder Backend)

## Input (prompt)
- Ticket T1: DB tablas pedidos, choferes, camiones + constraints. Objetivo: soporte en base de datos para pedidos (cliente, volquete, dirección de entrega, estado, fechas, asignación chofer/camión) y tablas mínimas choferes/camiones; constraint unicidad volquete en pedidos activos (NUEVO/ASIGNADO/ENTREGADO).
- Alcance: CREATE TABLE choferes (id, nombre, created_at), camiones (id, patente, created_at), pedidos (todos los campos + FKs, chk_pedidos_estado, índices, uk_pedidos_volquete_activo parcial). Script incremental + rollback. Fuente: memory-bank 01, 04, 06, backend.mdc.
- No alcance: lógica de aplicación; historial de estados de pedido en v1.

## Output
- Script incremental `db/scripts/20260204_3_pedidos.sql`: CREATE TABLE choferes, camiones, pedidos; FKs a clientes y volquetes; CHECK estado; CREATE UNIQUE INDEX uk_pedidos_volquete_activo WHERE estado IN ('NUEVO','ASIGNADO','ENTREGADO'); índices idx_pedidos_estado, idx_pedidos_cliente_id, idx_pedidos_volquete_id.
- Rollback `20260204_3_pedidos__rollback.sql`: DROP TABLE pedidos, camiones, choferes (en ese orden).
- Actualización 06-data-model.md con nombres concretos de scripts (20260204_3_pedidos.sql). Sin código Java (T1 solo DB).

## Decisión humana
Scripts aceptados; ejecución manual sobre esquema con clientes y volquetes ya creados.

## Impacto en el repo
- Creados `db/scripts/20260204_3_pedidos.sql`, `db/scripts/20260204_3_pedidos__rollback.sql`. Actualizado `memory-bank/06-data-model.md` (referencias a nombres de scripts).
