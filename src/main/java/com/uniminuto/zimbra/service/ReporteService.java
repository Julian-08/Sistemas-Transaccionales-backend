package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.ReporteRs;

import java.time.LocalDate;

/**
 * Reportes del administrador (RF-019).
 */
public interface ReporteService {

    /**
     * @param tipo visitas, prospectos, conversion o desempeno-asesor.
     */
    ReporteRs generar(String tipo, LocalDate desde, LocalDate hasta);

    /** Indicadores del panel del administrador (mes actual). */
    java.util.Map<String, Object> resumenAdministrador();

    /** Convierte el reporte a CSV para exportarlo (separador punto y coma, compatible con Excel). */
    String aCsv(ReporteRs reporte);
}
