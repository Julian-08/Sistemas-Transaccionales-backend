package com.uniminuto.zimbra.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Propuesta comercial con nombres de cliente y asesor.
 */
@Data
public class PropuestaRs {

    private Integer idPropuesta;
    private Integer idCliente;
    private String nombreCliente;
    private String empresa;
    private Integer idAsesor;
    private String nombreAsesor;
    private BigDecimal montoEstimado;
    private LocalDateTime fechaEmision;
    private String estado;
}
