-- Feature: Inventario de Volquetes (ABM + estados + historial)
-- Ticket: [T1] DB: Rollback manual (best-effort) tablas volquetes + volquete_estado_historial
-- Referencia: memory-bank/06-data-model.md
-- Orden: primero historial (tiene FK a volquetes), luego volquetes.

DROP INDEX IF EXISTS idx_volquete_estado_historial_volquete_id;
DROP TABLE IF EXISTS volquete_estado_historial;

DROP INDEX IF EXISTS idx_volquetes_estado_actual;
DROP TABLE IF EXISTS volquetes;
