package com.uniminuto.zimbra.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de "olvide mi contrasena" (RF-003).
 *
 * El codigo solo viene en la respuesta cuando zimbra.recuperacion.mostrar-codigo=true
 * (desarrollo, sin correo). En produccion va vacio y el codigo se envia por correo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecuperacionRs {

    private Boolean exito;

    private String mensaje;

    /** Codigo de recuperacion en claro (solo en desarrollo). */
    private String codigo;

    /** Minutos que dura el codigo. */
    private Integer minutosVigencia;
}
