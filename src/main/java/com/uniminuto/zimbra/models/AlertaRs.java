package com.uniminuto.zimbra.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Alerta para el asesor o el administrador (RF-008, RF-011).
 */
@Data
public class AlertaRs {

    private Integer idAlerta;
    private Integer idCliente;
    private String nombreCliente;
    private String empresa;
    private String mensaje;
    private LocalDateTime fechaHora;
    private Boolean leida;
}
