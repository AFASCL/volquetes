-- Feature: Gestión de Clientes (ABM + tipo común/abono)
-- T1 — DB: Rollback manual (best-effort) tabla clientes
-- Referencia: memory-bank/06-data-model.md
-- Ejecutar solo si no hay FKs desde otras tablas (ej. pedidos) referenciando clientes.

DROP INDEX IF EXISTS idx_clientes_nombre;
DROP TABLE IF EXISTS clientes;
