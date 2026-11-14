-- =====================================================================
-- crm_zimbra - Tabla de configuracion (RF-017)
-- Permite al administrador cambiar el puntaje minimo para que un
-- cliente pase a ser prospecto, sin reiniciar el sistema.
-- Ejecutar una sola vez sobre la base crm_zimbra (MySQL 8.0.16+).
-- =====================================================================
USE crm_zimbra;

CREATE TABLE IF NOT EXISTS configuracion (
    clave               VARCHAR(50)  NOT NULL,
    valor               VARCHAR(100) NOT NULL,
    descripcion         VARCHAR(255) NULL,
    fecha_actualizacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                     ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (clave)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO configuracion (clave, valor, descripcion)
VALUES ('PUNTAJE_MINIMO', '50', 'Puntaje minimo para clasificar a un cliente como prospecto (RF-017)'),
       ('DIAS_PRUEBA', '60', 'Dias de vigencia de la prueba gratuita de Zimbra')
ON DUPLICATE KEY UPDATE clave = clave;

-- El trigger ya no usa el 50 fijo: lee el puntaje minimo de configuracion.
DROP TRIGGER IF EXISTS trg_clientes_bu;
DELIMITER //
CREATE TRIGGER trg_clientes_bu
    BEFORE UPDATE ON clientes
    FOR EACH ROW
BEGIN
    DECLARE v_minimo INT;

    SELECT CAST(valor AS UNSIGNED) INTO v_minimo
      FROM configuracion
     WHERE clave = 'PUNTAJE_MINIMO';

    IF OLD.es_prospecto = FALSE AND NEW.puntaje >= IFNULL(v_minimo, 50) THEN
        SET NEW.es_prospecto = TRUE,
            NEW.estado_prospecto = 'NUEVO',
            NEW.fecha_clasificacion = NOW();
    END IF;
END //
DELIMITER ;
