package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Registro de un cliente desde el sitio web para descargar la prueba de Zimbra.
 */
@Data
public class RegistroClienteRq {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 100, message = "El correo no puede superar 100 caracteres.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String contrasena;

    @NotBlank(message = "La empresa es obligatoria.")
    @Size(max = 100, message = "La empresa no puede superar 100 caracteres.")
    private String empresa;
}
