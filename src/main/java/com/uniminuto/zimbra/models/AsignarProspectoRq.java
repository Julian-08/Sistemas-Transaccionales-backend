package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Asignar o reasignar un prospecto a un asesor (RF-016).
 */
@Data
public class AsignarProspectoRq {

    @NotNull(message = "El id del cliente es obligatorio.")
    private Integer idCliente;

    @NotNull(message = "El id del asesor es obligatorio.")
    private Integer idAsesor;
}
