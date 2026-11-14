# API backend CRM Zimbra

Base: `http://localhost:8081/zimbra/v1`

## Arquitectura (por capas, igual al proyecto de referencia)

| Paquete | Qué hace |
|---|---|
| `api` | Interfaces con las rutas, los roles permitidos y la documentación de cada endpoint |
| `apicontroller` | Implementan las interfaces `api` y llaman al servicio |
| `service` / `serviceimpl` | Reglas de negocio |
| `repository` | JPA por tabla, `ProcedimientoRepository` (llama los 15 procedimientos almacenados), `ConsultaRepository` (listados con nombres) y `ReporteRepository` |
| `models` | Datos de entrada (`...Rq`) y de salida (`...Rs`) |
| `entity` | Las 11 tablas originales + `Configuracion` (RF-017) |
| `security` | JWT, control por rol (`@RolesPermitidos`, `@Publico`) |
| `config`, `exception`, `util` | BCrypt, CORS, tareas en segundo plano, manejo de errores |

## Seguridad

- **Contraseñas (RNF-001):** se guardan con BCrypt (hash con sal, no reversible). Mínimo 8 caracteres con letras y números.
- **Sesión (RF-001, RNF-003):** `POST /auth/login` devuelve un `token`. Envíelo en cada petición: `Authorization: Bearer <token>`.
  El token vence a los 15 minutos de inactividad. Cada respuesta trae un token nuevo en el header `X-Token-Renovado`; el frontend debe reemplazar el anterior.
- **Roles (RNF-002):** un rol que llama a un endpoint de otro rol recibe 403. Sin token o con token vencido, 401.
- **Recuperación (RF-003):** en desarrollo el correo está apagado y el enlace se escribe en la consola del backend.

## Base de datos

Ejecutar una vez, en orden, sobre `crm_zimbra`:

1. `src/main/resources/db/01_configuracion.sql`: crea la tabla `configuracion` y cambia el trigger `trg_clientes_bu` para leer el puntaje mínimo.
2. `src/main/resources/db/02_usuarios_prueba.sql` (solo desarrollo): pone una contraseña de prueba real a los usuarios de ejemplo. La contraseña está en ese archivo.
3. `src/main/resources/db/03_configuracion_clientes.sql`: reglas de clientes listos para el plan de pago (`INGRESOS_CALIFICADO` = 50, `DIAS_CALIFICADO` = 60) y de inactivación (`DIAS_INACTIVIDAD` = 60).

## Endpoints

| Método | Ruta | Rol | RF |
|---|---|---|---|
| POST | `/auth/login` | público | RF-001, RF-004 |
| POST | `/auth/registro` | público | registro de cliente |
| POST | `/auth/recuperar-contrasena` (código de 8 caracteres, 15 min; en dev lo devuelve en `codigo`) | público | RF-003 |
| POST | `/auth/restablecer-contrasena` (`email`, `codigo`, `nuevaContrasena`) | público | RF-003 |
| POST | `/auth/cerrar-sesion` | todos | RF-001 |
| GET | `/auth/menu` · `/auth/sesion` | todos | RF-002 |
| GET | `/usuario/listar?idRol=` · `/usuario/{id}` · `/usuario/roles` | ADMINISTRADOR | RF-014 |
| POST | `/usuario/guardar` | ADMINISTRADOR | RF-014 |
| PUT | `/usuario/actualizar` · `/usuario/cambiar-estado` | ADMINISTRADOR | RF-014 |
| PUT | `/usuario/asignar-rol` | ADMINISTRADOR | RF-015 |
| GET | `/asesor/listar?soloActivos=` · PUT `/asesor/meta` | ADMINISTRADOR | |
| GET | `/asesor/mis-estadisticas` | ASESOR | panel del asesor |
| GET | `/cliente-admin/listar` · POST `/cliente-admin/crear` | ADMINISTRADOR | gestión de clientes |
| PUT | `/cliente-admin/{idCliente}/inactivar` · `/activar` · POST `/cliente-admin/inactivar-inactivos` | ADMINISTRADOR | los clientes no se borran; se inactivan tras 60 días sin ingresar y sin licencia |
| GET | `/cliente/perfil` · `/cliente/pruebas` | CLIENTE | |
| POST | `/cliente/descargar-prueba` | CLIENTE | descarga de prueba |
| POST | `/cliente/actividad` (responde 202) | CLIENTE | RF-005, RF-006 |
| GET | `/prospecto/mis-prospectos?estado=` | ASESOR | RF-009 |
| GET | `/prospecto/listar?idAsesor=&sinAsignar=&estado=` | ADMINISTRADOR | RF-016 |
| GET | `/prospecto/{idCliente}/actividad` | ASESOR, ADMINISTRADOR | RF-010 |
| PUT | `/prospecto/cambiar-estado` | ASESOR | RF-013 |
| PUT | `/prospecto/asignar` | ADMINISTRADOR | RF-016 |
| GET | `/prospecto/calificados?sinAsignar=` | ASESOR (los suyos), ADMINISTRADOR (todos) | clientes listos para el plan de pago |
| POST | `/seguimiento/registrar` | ASESOR | RF-012 |
| GET | `/seguimiento/prospecto/{idCliente}` | ASESOR, ADMINISTRADOR | RF-012 |
| GET | `/seguimiento/listar?idAsesor=&desde=&hasta=` | ADMINISTRADOR | RF-018 |
| GET | `/alerta/mis-alertas?soloNoLeidas=` · `/alerta/no-leidas/conteo` | ASESOR, ADMINISTRADOR | RF-008, RF-011 |
| PUT | `/alerta/{idAlerta}/marcar-leida` | ASESOR, ADMINISTRADOR | RF-011 |
| POST | `/propuesta/crear` · PUT `/propuesta/cambiar-estado` | ASESOR | propuestas RFP |
| GET | `/propuesta/listar?idCliente=&estado=` | ASESOR, ADMINISTRADOR | |
| POST | `/venta/registrar` | ASESOR | ventas |
| GET | `/venta/listar?desde=&hasta=` | ASESOR, ADMINISTRADOR | |
| GET/PUT | `/configuracion/puntaje-minimo` · GET `/configuracion/listar` · PUT `/configuracion/actualizar` | ADMINISTRADOR | RF-017 |
| GET | `/reporte/resumen` | ADMINISTRADOR | panel del administrador |
| GET | `/reporte/{tipo}?desde=&hasta=` | ADMINISTRADOR | RF-019 |
| GET | `/reporte/{tipo}/exportar?desde=&hasta=` (CSV) | ADMINISTRADOR | RF-019 |

Tipos de reporte: `visitas`, `prospectos`, `conversion`, `desempeno-asesor`. Fechas en formato `yyyy-MM-dd`.

## Flujo automatizado (caso Zimbra)

1. El cliente se registra y descarga la prueba de 60 días.
2. Cada ingreso y página visitada suma puntaje (trigger `trg_actividad_ai`): descarga 40, formulario 30, email 15, visita 10.
3. Al llegar al puntaje mínimo (tabla `configuracion`) pasa a prospecto `NUEVO` (trigger `trg_clientes_bu`).
4. El administrador lo asigna a un asesor activo y el asesor recibe una alerta.
5. Cuando el prospecto vuelve al sitio, el asesor recibe otra alerta.
6. El asesor registra seguimientos (el estado pasa a `CONTACTADO`), crea la propuesta y cierra la venta (la propuesta queda `ACEPTADA` y el prospecto `CERRADO`).

## Producción

Definir las variables de entorno `JWT_SECRET` (mínimo 32 caracteres), `MAIL_USERNAME`, `MAIL_PASSWORD` y `FRONTEND_URL`, y usar el perfil `prod`.
