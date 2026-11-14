package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Cambio de estado de un prospecto por su asesor (RF-013).
 */
@Data
public class CambiarEstadoProspectoRq {

    @NotNull(message = "El id del cliente es obligatorio.")
    private Integer idCliente;

    @NotBlank(message = "El estado es obligatorio.")
    @Pattern(regexp = "NUEVO|CONTACTADO|INTERESADO|CERRADO|DESCARTADO",
            message = "El estado debe ser NUEVO, CONTACTADO, INTERESADO, CERRADO o DESCARTADO.")
    private String estado;

    /** Debe ser true para modificar un prospecto que ya esta CERRADO (RF-013 A1). */
    private Boolean confirmar;
}
