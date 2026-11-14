-- =====================================================================
-- crm_zimbra - Contrasenas de los usuarios de prueba (SOLO DESARROLLO)
--
-- Los datos de ejemplo traian hashes de relleno ('$2b$12$PLACE...') con los
-- que nadie podia iniciar sesion. Este script los reemplaza por un hash BCrypt
-- real de la contrasena de prueba:  Zimbra2026
--
-- Aplica a todos los usuarios de ejemplo (admin@zimbra.test, asesores
-- *@zimbra.test y clientes *@empresa.test). No ejecutar en produccion.
-- =====================================================================
USE crm_zimbra;

UPDATE usuarios
   SET contrasena_hash = '$2a$10$Ig6GXcGr.IjyzftwoU6Gue7ArwERE/7DoFPGoPWNbZzq17gyG/P7C'
 WHERE contrasena_hash LIKE '$2b$12$PLACE%';
