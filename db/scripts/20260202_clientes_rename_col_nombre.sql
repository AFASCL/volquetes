-- Alinear DB con contrato: columna nombre (un solo campo nombre/razón social)
-- Renombra razon_social -> nombre. Índice idx_clientes_nombre sigue aplicado a la misma columna.

ALTER TABLE clientes RENAME COLUMN razon_social TO nombre;

COMMENT ON COLUMN clientes.nombre IS 'Nombre o razón social (no vacío validado en app).';
