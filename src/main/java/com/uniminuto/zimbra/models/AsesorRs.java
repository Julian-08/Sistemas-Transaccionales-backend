package com.uniminuto.zimbra.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Asesor comercial con su meta y cantidad de prospectos asignados.
 */
@Data
public class AsesorRs {

    private Integer idAsesor;
    private Integer idUsuario;
    private String nombre;
    private String email;
    private String estado;
    private BigDecimal metaMensual;
    private Integer prospectosAsignados;
}
