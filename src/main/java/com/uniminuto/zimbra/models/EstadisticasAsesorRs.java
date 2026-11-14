package com.uniminuto.zimbra.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Panel de estadisticas del asesor (mes actual).
 */
@Data
public class EstadisticasAsesorRs {

    private Integer prospectosAsignados;
    /** Cantidad de prospectos por estado (NUEVO, CONTACTADO, ...). */
    private Map<String, Integer> prospectosPorEstado;
    private Integer clientesCalificados;
    private Long alertasNoLeidas;
    private Integer seguimientosMes;
    private Integer propuestasAbiertas;
    private Integer ventasMes;
    private BigDecimal montoVendidoMes;
    private BigDecimal metaMensual;
    private BigDecimal cumplimientoMetaPct;
}
