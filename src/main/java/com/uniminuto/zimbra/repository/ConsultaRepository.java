package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.models.AlertaRs;
import com.uniminuto.zimbra.models.AsesorRs;
import com.uniminuto.zimbra.models.ClienteAdminRs;
import com.uniminuto.zimbra.models.ClienteCalificadoRs;
import com.uniminuto.zimbra.models.PropuestaRs;
import com.uniminuto.zimbra.models.ProspectoRs;
import com.uniminuto.zimbra.models.SeguimientoRs;
import com.uniminuto.zimbra.models.UsuarioRs;
import com.uniminuto.zimbra.models.VentaRs;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Consultas de solo lectura que unen varias tablas para mostrar nombres
 * (cliente, asesor, rol) en los listados. Las columnas se nombran igual que
 * los campos del modelo (snake_case a camelCase) para mapearlas automaticamente.
 */
@Repository
public class ConsultaRepository {

    private static final int LIMITE_ALERTAS = 200;

    private final JdbcTemplate jdbcTemplate;

    public ConsultaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ------------------------------------------------------------------ usuarios

    private static final String SQL_USUARIOS = """
            SELECT u.id_usuario, u.nombre, u.email, u.id_rol, r.nombre AS rol,
                   u.estado, u.fecha_creacion
              FROM usuarios u
              JOIN roles r ON r.id_rol = u.id_rol
            """;

    public List<UsuarioRs> listarUsuarios(Integer idRol) {
        if (idRol == null) {
            return jdbcTemplate.query(SQL_USUARIOS + " ORDER BY u.nombre", mapper(UsuarioRs.class));
        }
        return jdbcTemplate.query(SQL_USUARIOS + " WHERE u.id_rol = ? ORDER BY u.nombre",
                mapper(UsuarioRs.class), idRol);
    }

    public Optional<UsuarioRs> obtenerUsuario(Integer idUsuario) {
        return jdbcTemplate.query(SQL_USUARIOS + " WHERE u.id_usuario = ?", mapper(UsuarioRs.class), idUsuario)
                .stream().findFirst();
    }

    // ------------------------------------------------------------------ asesores

    public List<AsesorRs> listarAsesores(boolean soloActivos) {
        String sql = """
                SELECT a.id_asesor, a.id_usuario, u.nombre, u.email, u.estado, a.meta_mensual,
                       (SELECT COUNT(*) FROM clientes c
                         WHERE c.id_asesor = a.id_asesor AND c.es_prospecto = 1) AS prospectos_asignados
                  FROM asesor_comercial a
                  JOIN usuarios u ON u.id_usuario = a.id_usuario
                 WHERE u.id_rol = 2
                """ + (soloActivos ? " AND u.estado = 'ACTIVO'" : "") + " ORDER BY u.nombre";
        return jdbcTemplate.query(sql, mapper(AsesorRs.class));
    }

    // ---------------------------------------------------------------- prospectos

    private static final String SQL_PROSPECTOS = """
            SELECT c.id_cliente, c.id_usuario, u.nombre, u.email, c.empresa, c.puntaje,
                   c.estado_prospecto, c.fecha_clasificacion, c.id_asesor,
                   ua.nombre AS nombre_asesor,
                   (SELECT MAX(am.fecha_hora) FROM actividad_marketing am
                     WHERE am.id_cliente = c.id_cliente) AS ultima_actividad
              FROM clientes c
              JOIN usuarios u ON u.id_usuario = c.id_usuario
              LEFT JOIN asesor_comercial a ON a.id_asesor = c.id_asesor
              LEFT JOIN usuarios ua ON ua.id_usuario = a.id_usuario
             WHERE c.es_prospecto = 1
            """;

    /**
     * Lista prospectos con filtros opcionales.
     *
     * @param idAsesor   solo los de este asesor (null = todos).
     * @param sinAsignar true = solo los que no tienen asesor.
     * @param estado     estado del prospecto (null = todos).
     */
    public List<ProspectoRs> listarProspectos(Integer idAsesor, boolean sinAsignar, String estado) {
        StringBuilder sql = new StringBuilder(SQL_PROSPECTOS);
        List<Object> parametros = new ArrayList<>();
        if (idAsesor != null) {
            sql.append(" AND c.id_asesor = ?");
            parametros.add(idAsesor);
        }
        if (sinAsignar) {
            sql.append(" AND c.id_asesor IS NULL");
        }
        if (estado != null && !estado.isBlank()) {
            sql.append(" AND c.estado_prospecto = ?");
            parametros.add(estado);
        }
        sql.append(" ORDER BY c.puntaje DESC, c.fecha_clasificacion DESC");
        return jdbcTemplate.query(sql.toString(), mapper(ProspectoRs.class), parametros.toArray());
    }

    public Optional<ProspectoRs> obtenerProspecto(Integer idCliente) {
        return jdbcTemplate.query(SQL_PROSPECTOS + " AND c.id_cliente = ?", mapper(ProspectoRs.class), idCliente)
                .stream().findFirst();
    }

    // ------------------------------------------------------------- seguimientos

    /**
     * Seguimientos con filtros opcionales por cliente, asesor y rango de fechas (RF-012, RF-018).
     */
    public List<SeguimientoRs> listarSeguimientos(Integer idCliente, Integer idAsesor,
                                                  LocalDate desde, LocalDate hasta) {
        StringBuilder sql = new StringBuilder("""
                SELECT s.id_seguimiento, s.id_cliente, uc.nombre AS nombre_cliente, c.empresa,
                       s.id_asesor, ua.nombre AS nombre_asesor, s.fecha_seguimiento,
                       s.medio, s.observaciones
                  FROM seguimientos s
                  JOIN clientes c ON c.id_cliente = s.id_cliente
                  JOIN usuarios uc ON uc.id_usuario = c.id_usuario
                  JOIN asesor_comercial a ON a.id_asesor = s.id_asesor
                  JOIN usuarios ua ON ua.id_usuario = a.id_usuario
                 WHERE 1 = 1
                """);
        List<Object> parametros = new ArrayList<>();
        if (idCliente != null) {
            sql.append(" AND s.id_cliente = ?");
            parametros.add(idCliente);
        }
        if (idAsesor != null) {
            sql.append(" AND s.id_asesor = ?");
            parametros.add(idAsesor);
        }
        agregarRangoFechas(sql, parametros, "s.fecha_seguimiento", desde, hasta);
        sql.append(" ORDER BY s.fecha_seguimiento DESC");
        return jdbcTemplate.query(sql.toString(), mapper(SeguimientoRs.class), parametros.toArray());
    }

    // ------------------------------------------------------------------ alertas

    /** Alertas del usuario: las no leidas primero y luego las mas recientes (RF-011). */
    public List<AlertaRs> listarAlertas(Integer idUsuarioDestino, boolean soloNoLeidas) {
        String sql = """
                SELECT al.id_alerta, al.id_cliente, uc.nombre AS nombre_cliente, c.empresa,
                       al.mensaje, al.fecha_hora, al.leida
                  FROM alertas al
                  JOIN clientes c ON c.id_cliente = al.id_cliente
                  JOIN usuarios uc ON uc.id_usuario = c.id_usuario
                 WHERE al.id_usuario_destino = ?
                """ + (soloNoLeidas ? " AND al.leida = 0" : "")
                + " ORDER BY al.leida ASC, al.fecha_hora DESC LIMIT " + LIMITE_ALERTAS;
        return jdbcTemplate.query(sql, mapper(AlertaRs.class), idUsuarioDestino);
    }

    // --------------------------------------------------------------- propuestas

    public List<PropuestaRs> listarPropuestas(Integer idAsesor, Integer idCliente, String estado) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.id_propuesta, p.id_cliente, uc.nombre AS nombre_cliente, c.empresa,
                       p.id_asesor, ua.nombre AS nombre_asesor, p.monto_estimado,
                       p.fecha_emision, p.estado
                  FROM propuestas_rfp p
                  JOIN clientes c ON c.id_cliente = p.id_cliente
                  JOIN usuarios uc ON uc.id_usuario = c.id_usuario
                  JOIN asesor_comercial a ON a.id_asesor = p.id_asesor
                  JOIN usuarios ua ON ua.id_usuario = a.id_usuario
                 WHERE 1 = 1
                """);
        List<Object> parametros = new ArrayList<>();
        if (idAsesor != null) {
            sql.append(" AND p.id_asesor = ?");
            parametros.add(idAsesor);
        }
        if (idCliente != null) {
            sql.append(" AND p.id_cliente = ?");
            parametros.add(idCliente);
        }
        if (estado != null && !estado.isBlank()) {
            sql.append(" AND p.estado = ?");
            parametros.add(estado);
        }
        sql.append(" ORDER BY p.fecha_emision DESC");
        return jdbcTemplate.query(sql.toString(), mapper(PropuestaRs.class), parametros.toArray());
    }

    // ------------------------------------------------------------------- ventas

    public List<VentaRs> listarVentas(Integer idAsesor, LocalDate desde, LocalDate hasta) {
        StringBuilder sql = new StringBuilder("""
                SELECT v.id_venta, v.id_propuesta, v.id_cliente, uc.nombre AS nombre_cliente, c.empresa,
                       v.id_asesor, ua.nombre AS nombre_asesor, v.monto_final, v.fecha_cierre,
                       v.tipo_licencia, v.periodo
                  FROM ventas_licencias v
                  JOIN clientes c ON c.id_cliente = v.id_cliente
                  JOIN usuarios uc ON uc.id_usuario = c.id_usuario
                  JOIN asesor_comercial a ON a.id_asesor = v.id_asesor
                  JOIN usuarios ua ON ua.id_usuario = a.id_usuario
                 WHERE 1 = 1
                """);
        List<Object> parametros = new ArrayList<>();
        if (idAsesor != null) {
            sql.append(" AND v.id_asesor = ?");
            parametros.add(idAsesor);
        }
        agregarRangoFechas(sql, parametros, "v.fecha_cierre", desde, hasta);
        sql.append(" ORDER BY v.fecha_cierre DESC");
        return jdbcTemplate.query(sql.toString(), mapper(VentaRs.class), parametros.toArray());
    }

    // ------------------------------------------------------ clientes calificados

    /**
     * Clientes activos con al menos {@code minIngresos} ingresos en los ultimos {@code dias} dias.
     *
     * @param idAsesor   solo los de este asesor (null = todos).
     * @param sinAsignar true = solo los que no tienen asesor.
     */
    public List<ClienteCalificadoRs> listarCalificados(Integer idAsesor, boolean sinAsignar,
                                                       int minIngresos, int dias) {
        StringBuilder sql = new StringBuilder("""
                SELECT c.id_cliente, c.id_usuario, u.nombre, u.email, c.empresa, c.puntaje,
                       c.es_prospecto, c.estado_prospecto, c.id_asesor, ua.nombre AS nombre_asesor,
                       i.ingresos_periodo, i.ultimo_ingreso,
                       EXISTS (SELECT 1 FROM pruebas_descarga p
                                WHERE p.id_cliente = c.id_cliente AND p.fecha_vencimiento > NOW()) AS prueba_vigente,
                       (SELECT MAX(p.fecha_vencimiento) FROM pruebas_descarga p
                         WHERE p.id_cliente = c.id_cliente) AS fecha_vencimiento_prueba,
                       EXISTS (SELECT 1 FROM ventas_licencias v WHERE v.id_cliente = c.id_cliente) AS tiene_licencia
                  FROM clientes c
                  JOIN usuarios u ON u.id_usuario = c.id_usuario
                  JOIN (SELECT id_usuario, COUNT(*) AS ingresos_periodo, MAX(fecha_hora) AS ultimo_ingreso
                          FROM ingresos
                         WHERE fecha_hora >= NOW() - INTERVAL ? DAY
                         GROUP BY id_usuario
                        HAVING COUNT(*) >= ?) i ON i.id_usuario = c.id_usuario
                  LEFT JOIN asesor_comercial a ON a.id_asesor = c.id_asesor
                  LEFT JOIN usuarios ua ON ua.id_usuario = a.id_usuario
                 WHERE u.estado = 'ACTIVO'
                """);
        List<Object> parametros = new ArrayList<>(List.of(dias, minIngresos));
        if (idAsesor != null) {
            sql.append(" AND c.id_asesor = ?");
            parametros.add(idAsesor);
        }
        if (sinAsignar) {
            sql.append(" AND c.id_asesor IS NULL");
        }
        sql.append(" ORDER BY i.ingresos_periodo DESC, c.puntaje DESC");
        return jdbcTemplate.query(sql.toString(), mapper(ClienteCalificadoRs.class), parametros.toArray());
    }

    // --------------------------------------------------- clientes (administrador)

    private static final String SQL_CLIENTES_ADMIN = """
            SELECT c.id_cliente, c.id_usuario, u.nombre, u.email, c.empresa, u.estado, c.puntaje,
                   c.es_prospecto, c.estado_prospecto, c.id_asesor, ua.nombre AS nombre_asesor,
                   c.fecha_registro, ing.ultimo_ingreso,
                   COALESCE(ing.ingresos_periodo, 0) AS ingresos_periodo,
                   EXISTS (SELECT 1 FROM ventas_licencias v WHERE v.id_cliente = c.id_cliente) AS tiene_licencia,
                   DATEDIFF(NOW(), COALESCE(ing.ultimo_ingreso, c.fecha_registro)) AS dias_sin_ingresar
              FROM clientes c
              JOIN usuarios u ON u.id_usuario = c.id_usuario
              LEFT JOIN (SELECT id_usuario, MAX(fecha_hora) AS ultimo_ingreso,
                                SUM(fecha_hora >= NOW() - INTERVAL ? DAY) AS ingresos_periodo
                           FROM ingresos GROUP BY id_usuario) ing ON ing.id_usuario = c.id_usuario
              LEFT JOIN asesor_comercial a ON a.id_asesor = c.id_asesor
              LEFT JOIN usuarios ua ON ua.id_usuario = a.id_usuario
            """;

    /** Todos los clientes con su ultimo ingreso y licencia. */
    public List<ClienteAdminRs> listarClientesAdmin(int diasVentana) {
        return jdbcTemplate.query(SQL_CLIENTES_ADMIN + " ORDER BY u.nombre",
                mapper(ClienteAdminRs.class), diasVentana);
    }

    public Optional<ClienteAdminRs> obtenerClienteAdmin(Integer idCliente, int diasVentana) {
        return jdbcTemplate.query(SQL_CLIENTES_ADMIN + " WHERE c.id_cliente = ?",
                mapper(ClienteAdminRs.class), diasVentana, idCliente).stream().findFirst();
    }

    // ------------------------------------------------- estadisticas del asesor

    /** Prospectos del asesor agrupados por estado. */
    public List<Map<String, Object>> prospectosPorEstado(Integer idAsesor) {
        return jdbcTemplate.queryForList("""
                SELECT COALESCE(estado_prospecto, 'SIN_ESTADO') AS estado, COUNT(*) AS cantidad
                  FROM clientes
                 WHERE id_asesor = ? AND es_prospecto = 1
                 GROUP BY COALESCE(estado_prospecto, 'SIN_ESTADO')
                """, idAsesor);
    }

    /** Seguimientos, propuestas abiertas, ventas y monto del asesor desde la fecha indicada. */
    public Map<String, Object> resumenAsesor(Integer idAsesor, LocalDate desde) {
        return jdbcTemplate.queryForMap("""
                SELECT (SELECT COUNT(*) FROM seguimientos
                         WHERE id_asesor = ? AND fecha_seguimiento >= ?) AS seguimientos_mes,
                       (SELECT COUNT(*) FROM propuestas_rfp
                         WHERE id_asesor = ? AND estado IN ('ENVIADA', 'EN_REVISION')) AS propuestas_abiertas,
                       (SELECT COUNT(*) FROM ventas_licencias
                         WHERE id_asesor = ? AND fecha_cierre >= ?) AS ventas_mes,
                       (SELECT COALESCE(SUM(monto_final), 0) FROM ventas_licencias
                         WHERE id_asesor = ? AND fecha_cierre >= ?) AS monto_mes
                """, idAsesor, desde.atStartOfDay(), idAsesor, idAsesor, desde.atStartOfDay(),
                idAsesor, desde.atStartOfDay());
    }

    // ---------------------------------------------------------------- utilidades

    /** Agrega "columna >= desde" y "columna < hasta + 1 dia" (hasta incluido). */
    static void agregarRangoFechas(StringBuilder sql, List<Object> parametros, String columna,
                                   LocalDate desde, LocalDate hasta) {
        if (desde != null) {
            sql.append(" AND ").append(columna).append(" >= ?");
            parametros.add(desde.atStartOfDay());
        }
        if (hasta != null) {
            sql.append(" AND ").append(columna).append(" < ?");
            parametros.add(hasta.plusDays(1).atStartOfDay());
        }
    }

    private static <T> BeanPropertyRowMapper<T> mapper(Class<T> tipo) {
        return new BeanPropertyRowMapper<>(tipo);
    }
}
