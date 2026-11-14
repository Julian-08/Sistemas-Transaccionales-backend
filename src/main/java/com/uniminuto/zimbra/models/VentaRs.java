package com.uniminuto.zimbra.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Venta de licencias con nombres de cliente y asesor.
 */
@Data
public class VentaRs {

    private Integer idVenta;
    private Integer idPropuesta;
    private Integer idCliente;
    private String nombreCliente;
    private String empresa;
    private Integer idAsesor;
    private String nombreAsesor;
    private BigDecimal montoFinal;
    private LocalDateTime fechaCierre;
    private String tipoLicencia;
    private String periodo;
}
