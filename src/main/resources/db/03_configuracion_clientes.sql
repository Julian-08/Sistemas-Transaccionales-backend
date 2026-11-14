-- =====================================================================
-- crm_zimbra - Reglas de clientes calificados e inactivos
-- Ejecutar despues de 01_configuracion.sql.
--
-- INGRESOS_CALIFICADO : ingresos minimos para ofrecer el plan de pago
-- DIAS_CALIFICADO     : ventana de dias en la que se cuentan esos ingresos
-- DIAS_INACTIVIDAD    : dias sin ingresar (y sin licencia) para poder
--                       inactivar a un cliente. Los clientes nunca se borran.
-- =====================================================================
USE crm_zimbra;

INSERT INTO configuracion (clave, valor, descripcion)
VALUES ('INGRESOS_CALIFICADO', '50', 'Ingresos minimos en la ventana para que el cliente este listo para el plan de pago'),
       ('DIAS_CALIFICADO', '60', 'Dias en los que se cuentan los ingresos del cliente calificado'),
       ('DIAS_INACTIVIDAD', '60', 'Dias sin ingresar y sin licencia para poder inactivar a un cliente')
ON DUPLICATE KEY UPDATE clave = clave;

-- Indice para contar rapido los ingresos por usuario y fecha (RNF-012).
SET @existe := (SELECT COUNT(*) FROM information_schema.STATISTICS
                 WHERE TABLE_SCHEMA = 'crm_zimbra' AND TABLE_NAME = 'ingresos'
                   AND INDEX_NAME = 'idx_ingresos_usuario_fecha');
SET @sql := IF(@existe = 0,
               'CREATE INDEX idx_ingresos_usuario_fecha ON ingresos (id_usuario, fecha_hora)',
               'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
