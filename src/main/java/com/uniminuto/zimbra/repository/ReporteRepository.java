package com.uniminuto.zimbra.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Consultas de los reportes del administrador (RF-019).
 * Todas reciben el rango [inicio, fin): inicio incluido, fin excluido.
 */
@Repository
public class ReporteRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReporteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Visitas por dia: ingresos al sistema y eventos de navegacion por tipo. */
    public List<Map<String, Object>> visitas(LocalDateTime inicio, LocalDateTime fin) {
        String sql = """
                SELECT DATE_FORMAT(d.fecha, '%Y-%m-%d') AS fecha,
                       COALESCE(i.ingresos, 0)          AS ingresos,
                       COALESCE(a.total_eventos, 0)     AS total_eventos,
                       COALESCE(a.visitas_web, 0)       AS visitas_web,
                       COALESCE(a.formularios, 0)       AS formularios,
                       COALESCE(a.emails, 0)            AS emails,
                       COALESCE(a.descargas, 0)         AS descargas,
                       COALESCE(a.clientes_unicos, 0)   AS clientes_unicos,
                       COALESCE(a.tiempo_promedio_seg, 0) AS tiempo_promedio_seg
                  FROM (SELECT DATE(fecha_hora) AS fecha FROM ingresos
                         WHERE fecha_hora >= ? AND fecha_hora < ?
                        UNION
                        SELECT DATE(fecha_hora) FROM actividad_marketing
                         WHERE fecha_hora >= ? AND fecha_hora < ?) d
                  LEFT JOIN (SELECT DATE(fecha_hora) AS fecha, COUNT(*) AS ingresos
                               FROM ingresos
                              WHERE fecha_hora >= ? AND fecha_hora < ?
                              GROUP BY DATE(fecha_hora)) i ON i.fecha = d.fecha
                  LEFT JOIN (SELECT DATE(fecha_hora) AS fecha,
                                    COUNT(*) AS total_eventos,
                                    SUM(tipo_evento = 'VISITA_WEB') AS visitas_web,
                                    SUM(tipo_evento = 'FORMULARIO') AS formularios,
                                    SUM(tipo_evento = 'EMAIL') AS emails,
                                    SUM(tipo_evento = 'DESCARGA') AS descargas,
                                    COUNT(DISTINCT id_cliente) AS clientes_unicos,
                                    ROUND(AVG(tiempo_permanencia_seg)) AS tiempo_promedio_seg
                               FROM actividad_marketing
                              WHERE fecha_hora >= ? AND fecha_hora < ?
                              GROUP BY DATE(fecha_hora)) a ON a.fecha = d.fecha
                 ORDER BY d.fecha
                """;
        return jdbcTemplate.queryForList(sql, inicio, fin, inicio, fin, inicio, fin, inicio, fin);
    }

    /** Prospectos clasificados en el rango, agrupados por estado. */
    public List<Map<String, Object>> prospectos(LocalDateTime inicio, LocalDateTime fin) {
        String sql = """
                SELECT COALESCE(estado_prospecto, 'SIN_ESTADO') AS estado,
                       COUNT(*) AS cantidad,
                       ROUND(AVG(puntaje)) AS puntaje_promedio,
                       SUM(id_asesor IS NULL) AS sin_asesor
                  FROM clientes
                 WHERE es_prospecto = 1
                   AND fecha_clasificacion >= ? AND fecha_clasificacion < ?
                 GROUP BY COALESCE(estado_prospecto, 'SIN_ESTADO')
                 ORDER BY cantidad DESC
                """;
        return jdbcTemplate.queryForList(sql, inicio, fin);
    }

    /** Embudo de conversion: registro, prueba, prospecto, propuesta y venta. */
    public List<Map<String, Object>> conversion(LocalDateTime inicio, LocalDateTime fin) {
        String sql = """
                SELECT t.*,
                       ROUND(100 * t.ventas_cerradas / NULLIF(t.prospectos_clasificados, 0), 2)
                           AS tasa_conversion_pct
                  FROM (SELECT
                          (SELECT COUNT(*) FROM clientes
                            WHERE fecha_registro >= ? AND fecha_registro < ?) AS clientes_registrados,
                          (SELECT COUNT(*) FROM pruebas_descarga
                            WHERE fecha_inicio >= ? AND fecha_inicio < ?) AS pruebas_descargadas,
                          (SELECT COUNT(*) FROM clientes
                            WHERE es_prospecto = 1
                              AND fecha_clasificacion >= ? AND fecha_clasificacion < ?) AS prospectos_clasificados,
                          (SELECT COUNT(*) FROM propuestas_rfp
                            WHERE fecha_emision >= ? AND fecha_emision < ?) AS propuestas_emitidas,
                          (SELECT COUNT(*) FROM ventas_licencias
                            WHERE fecha_cierre >= ? AND fecha_cierre < ?) AS ventas_cerradas,
                          (SELECT COALESCE(SUM(monto_final), 0) FROM ventas_licencias
                            WHERE fecha_cierre >= ? AND fecha_cierre < ?) AS monto_vendido
                       ) t
                """;
        return jdbcTemplate.queryForList(sql,
                inicio, fin, inicio, fin, inicio, fin, inicio, fin, inicio, fin, inicio, fin);
    }

    /**
     * Indicadores del panel del administrador.
     *
     * @param minIngresos ingresos minimos de un cliente calificado.
     * @param dias        ventana de dias de los ingresos.
     * @param inicioMes   primer dia del mes actual.
     */
    public Map<String, Object> resumenAdministrador(int minIngresos, int dias, LocalDateTime inicioMes) {
        String sql = """
                SELECT
                  (SELECT COUNT(*) FROM clientes) AS total_clientes,
                  (SELECT COUNT(*) FROM clientes c JOIN usuarios u ON u.id_usuario = c.id_usuario
                    WHERE u.estado = 'ACTIVO') AS clientes_activos,
                  (SELECT COUNT(*) FROM clientes WHERE es_prospecto = 1) AS prospectos,
                  (SELECT COUNT(*) FROM clientes WHERE es_prospecto = 1 AND id_asesor IS NULL) AS prospectos_sin_asignar,
                  (SELECT COUNT(*) FROM (SELECT i.id_usuario FROM ingresos i
                                           JOIN usuarios u ON u.id_usuario = i.id_usuario
                                          WHERE u.id_rol = 3 AND u.estado = 'ACTIVO'
                                            AND i.fecha_hora >= NOW() - INTERVAL ? DAY
                                          GROUP BY i.id_usuario HAVING COUNT(*) >= ?) q) AS clientes_calificados,
                  (SELECT COUNT(*) FROM asesor_comercial a JOIN usuarios u ON u.id_usuario = a.id_usuario
                    WHERE u.id_rol = 2 AND u.estado = 'ACTIVO') AS asesores_activos,
                  (SELECT COUNT(*) FROM pruebas_descarga WHERE fecha_vencimiento > NOW()) AS pruebas_vigentes,
                  (SELECT COUNT(*) FROM ventas_licencias WHERE fecha_cierre >= ?) AS ventas_mes,
                  (SELECT COALESCE(SUM(monto_final), 0) FROM ventas_licencias WHERE fecha_cierre >= ?) AS monto_mes
                """;
        return jdbcTemplate.queryForMap(sql, dias, minIngresos, inicioMes, inicioMes);
    }

    /** Desempeno por asesor: seguimientos, propuestas, ventas y monto vendido en el rango. */
    public List<Map<String, Object>> desempenoAsesor(LocalDateTime inicio, LocalDateTime fin) {
        String sql = """
                SELECT a.id_asesor,
                       u.nombre AS asesor,
                       u.estado,
                       a.meta_mensual,
                       (SELECT COUNT(*) FROM clientes c
                         WHERE c.id_asesor = a.id_asesor AND c.es_prospecto = 1) AS prospectos_asignados,
                       (SELECT COUNT(*) FROM seguimientos s
                         WHERE s.id_asesor = a.id_asesor
                           AND s.fecha_seguimiento >= ? AND s.fecha_seguimiento < ?) AS seguimientos,
                       (SELECT COUNT(*) FROM propuestas_rfp p
                         WHERE p.id_asesor = a.id_asesor
                           AND p.fecha_emision >= ? AND p.fecha_emision < ?) AS propuestas,
                       (SELECT COUNT(*) FROM ventas_licencias v
                         WHERE v.id_asesor = a.id_asesor
                           AND v.fecha_cierre >= ? AND v.fecha_cierre < ?) AS ventas,
                       (SELECT COALESCE(SUM(v.monto_final), 0) FROM ventas_licencias v
                         WHERE v.id_asesor = a.id_asesor
                           AND v.fecha_cierre >= ? AND v.fecha_cierre < ?) AS monto_vendido
                  FROM asesor_comercial a
                  JOIN usuarios u ON u.id_usuario = a.id_usuario
                 WHERE u.id_rol = 2
                 ORDER BY monto_vendido DESC, u.nombre
                """;
        return jdbcTemplate.queryForList(sql, inicio, fin, inicio, fin, inicio, fin, inicio, fin);
    }
}
