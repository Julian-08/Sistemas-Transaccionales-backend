package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.ReporteRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Reportes del administrador (RF-019).
 * Tipos: visitas, prospectos, conversion, desempeno-asesor.
 * Si no se envian fechas se usan los ultimos 30 dias.
 */
@RequestMapping("/reporte")
@RolesPermitidos({Roles.ADMINISTRADOR})
public interface ReporteApi {

    /**
     * Indicadores del panel del administrador: clientes, prospectos, calificados,
     * asesores activos, pruebas vigentes y ventas del mes.
     */
    @GetMapping(value = "/resumen", produces = "application/json")
    ResponseEntity<java.util.Map<String, Object>> resumen();

    /**
     * Genera el reporte en JSON.
     */
    @GetMapping(value = "/{tipo}", produces = "application/json")
    ResponseEntity<ReporteRs> generar(
            @PathVariable("tipo") String tipo,
            @RequestParam(value = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta);

    /**
     * Exporta el reporte como archivo CSV (se abre en Excel).
     */
    @GetMapping(value = "/{tipo}/exportar", produces = "text/csv")
    ResponseEntity<byte[]> exportar(
            @PathVariable("tipo") String tipo,
            @RequestParam(value = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta);
}
