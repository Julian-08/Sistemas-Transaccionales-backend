package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos para que el administrador edite un usuario (RF-014).
 * Si se envia contrasena, se reemplaza (cifrada con BCrypt); si no, se conserva.
 */
@Data
public class UsuarioActualizarRq {

    @NotNull(message = "El id del usuario es obligatorio.")
    private Integer idUsuario;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 100, message = "El correo no puede superar 100 caracteres.")
    private String email;

    /** Opcional: nueva contrasena. */
    private String contrasena;
}
