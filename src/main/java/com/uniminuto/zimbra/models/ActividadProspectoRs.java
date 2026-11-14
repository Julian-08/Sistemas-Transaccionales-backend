package com.uniminuto.zimbra.models;

import com.uniminuto.zimbra.entity.ActividadMarketing;
import com.uniminuto.zimbra.entity.Ingreso;
import lombok.Data;

import java.util.List;

/**
 * Detalle de actividad de un prospecto para el asesor (RF-010):
 * ingresos, paginas visitadas, puntaje e historial de seguimientos.
 */
@Data
public class ActividadProspectoRs {

    private ProspectoRs prospecto;
    private Long totalIngresos;
    private List<Ingreso> ingresos;
    private List<ActividadMarketing> actividades;
    private List<SeguimientoRs> seguimientos;
    /** Mensaje informativo cuando no hay actividad (RF-010 A1). */
    private String mensaje;
}
