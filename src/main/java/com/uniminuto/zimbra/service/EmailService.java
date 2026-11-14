package com.uniminuto.zimbra.service;

/**
 * Envio de correos del sistema (HTML con la marca de Zimbra).
 */
public interface EmailService {

    /** RF-003: envia el codigo para restablecer la contrasena. */
    void enviarCodigoRecuperacion(String email, String nombre, String codigo, int minutosVigencia);

    /** Bienvenida al cliente que se registra (o que crea el administrador). */
    void enviarBienvenida(String email, String nombre, String empresa);
}
