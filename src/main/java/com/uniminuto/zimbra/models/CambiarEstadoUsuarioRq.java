package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Activar o desactivar un usuario (RF-014). Un usuario inactivo no puede iniciar sesion.
 */
@Data
public class CambiarEstadoUsuarioRq {

    @NotNull(message = "El id del usuario es obligatorio.")
    private Integer idUsuario;

    @NotBlank(message = "El estado es obligatorio.")
    @Pattern(regexp = "ACTIVO|INACTIVO", message = "El estado debe ser ACTIVO o INACTIVO.")
    private String estado;
}
