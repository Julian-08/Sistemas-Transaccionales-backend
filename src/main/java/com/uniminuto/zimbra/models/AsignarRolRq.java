package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Asignar el rol de un usuario (RF-015). 1 = administrador, 2 = asesor, 3 = cliente.
 */
@Data
public class AsignarRolRq {

    @NotNull(message = "El id del usuario es obligatorio.")
    private Integer idUsuario;

    @NotNull(message = "El rol es obligatorio.")
    @Min(value = 1, message = "El rol no es válido.")
    @Max(value = 3, message = "El rol no es válido.")
    private Integer idRol;
}
