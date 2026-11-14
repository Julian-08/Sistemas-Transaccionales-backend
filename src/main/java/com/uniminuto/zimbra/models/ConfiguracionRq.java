package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Cambio de un parametro numerico de configuracion.
 */
@Data
public class ConfiguracionRq {

    @NotBlank(message = "La clave es obligatoria.")
    private String clave;

    @NotBlank(message = "El valor es obligatorio.")
    private String valor;
}
