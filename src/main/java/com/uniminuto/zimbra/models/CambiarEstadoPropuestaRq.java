package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Cambio de estado de una propuesta. ACEPTADA solo se pone al registrar la venta.
 */
@Data
public class CambiarEstadoPropuestaRq {

    @NotNull(message = "El id de la propuesta es obligatorio.")
    private Integer idPropuesta;

    @NotBlank(message = "El estado es obligatorio.")
    @Pattern(regexp = "EN_REVISION|RECHAZADA", message = "El estado debe ser EN_REVISION o RECHAZADA.")
    private String estado;
}
