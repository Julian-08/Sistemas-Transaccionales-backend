package com.uniminuto.zimbra.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Prueba gratuita de Zimbra descargada por el cliente.
 */
@Data
public class PruebaRs {

    private Integer idPrueba;
    private LocalDateTime fechaInicio;
    private Integer diasPrueba;
    private LocalDateTime fechaVencimiento;
    private Boolean vigente;
    private Long diasRestantes;
}
