-- Rollback: nombre -> razon_social

ALTER TABLE clientes RENAME COLUMN nombre TO razon_social;

COMMENT ON COLUMN clientes.razon_social IS 'Nombre o razón social (no vacío validado en app).';
