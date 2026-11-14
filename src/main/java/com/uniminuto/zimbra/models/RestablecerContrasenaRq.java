package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Nueva contrasena con el codigo de recuperacion (RF-003).
 * El usuario escribe su correo, el codigo de 8 caracteres y la nueva contrasena.
 */
@Data
public class RestablecerContrasenaRq {

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    private String email;

    @NotBlank(message = "El código de recuperación es obligatorio.")
    private String codigo;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    private String nuevaContrasena;
}
