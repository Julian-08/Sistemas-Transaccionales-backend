package com.uniminuto.zimbra.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Resultado de un reporte (RF-019). Las filas se pueden exportar a CSV.
 */
@Data
public class ReporteRs {

    /** visitas, prospectos, conversion o desempeno-asesor. */
    private String tipo;
    private LocalDate desde;
    private LocalDate hasta;
    private List<String> columnas;
    private List<Map<String, Object>> filas;
    /** Mensaje cuando no hay datos en el rango (RF-019 A1). */
    private String mensaje;
}
