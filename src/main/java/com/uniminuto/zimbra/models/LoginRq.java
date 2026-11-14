package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Datos para iniciar sesion (RF-001).
 */
@Data
public class LoginRq {

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String contrasena;
}
