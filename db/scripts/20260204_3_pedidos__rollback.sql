-- Feature: Gestión de Pedidos (Issue 3)
-- Ticket: T1 — Rollback manual tablas pedidos, camiones, choferes
-- Referencia: memory-bank/06-data-model.md
-- Orden: primero pedidos (tiene FK a choferes y camiones), luego camiones, luego choferes.

DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS camiones;
DROP TABLE IF EXISTS choferes;
