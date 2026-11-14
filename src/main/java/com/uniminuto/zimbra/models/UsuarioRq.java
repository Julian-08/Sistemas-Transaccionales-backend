package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Datos para que el administrador cree un usuario (RF-014).
 * Solo se crean administradores (idRol 1) o asesores (idRol 2);
 * los clientes se registran solos desde el sitio web.
 */
@Data
public class UsuarioRq {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 100, message = "El correo no puede superar 100 caracteres.")
    private String email;

    /** Contrasena en texto plano: se cifra con BCrypt antes de guardarse. */
    @NotBlank(message = "La contraseña es obligatoria.")
    private String contrasena;

    @NotNull(message = "El rol es obligatorio.")
    private Integer idRol;

    /** Meta mensual de ventas, solo para asesores. */
    @PositiveOrZero(message = "La meta mensual no puede ser negativa.")
    private BigDecimal metaMensual;
}
