package com.uniminuto.zimbra.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Seguimiento con nombres de cliente y asesor (RF-012, RF-018).
 */
@Data
public class SeguimientoRs {

    private Integer idSeguimiento;
    private Integer idCliente;
    private String nombreCliente;
    private String empresa;
    private Integer idAsesor;
    private String nombreAsesor;
    private LocalDateTime fechaSeguimiento;
    private String medio;
    private String observaciones;
}
