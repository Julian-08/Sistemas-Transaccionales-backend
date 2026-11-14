package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Registro de un contacto del asesor con su prospecto (RF-012).
 * La fecha la pone la base de datos en el momento del registro.
 */
@Data
public class SeguimientoRq {

    @NotNull(message = "El id del cliente es obligatorio.")
    private Integer idCliente;

    @NotBlank(message = "El medio es obligatorio.")
    @Pattern(regexp = "LLAMADA|CORREO", message = "El medio debe ser LLAMADA o CORREO.")
    private String medio;

    @NotBlank(message = "Las observaciones son obligatorias.")
    private String observaciones;
}
