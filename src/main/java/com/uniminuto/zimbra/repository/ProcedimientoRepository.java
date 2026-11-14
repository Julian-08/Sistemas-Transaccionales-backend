package com.uniminuto.zimbra.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Llama a los procedimientos almacenados de crm_zimbra.
 * Las reglas de negocio y las transacciones viven en la base de datos; si una regla
 * no se cumple, el procedimiento lanza SIGNAL SQLSTATE 45000 y el
 * GlobalExceptionHandler lo devuelve como error 400 con el mensaje del procedimiento.
 * Por eso estos metodos no se envuelven en @Transactional: cada procedimiento
 * maneja su propia transaccion (START TRANSACTION / COMMIT / ROLLBACK).
 */
@Repository
public class ProcedimientoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProcedimientoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** sp_registrar_cliente: crea usuario con rol CLIENTE y su ficha en clientes. */
    public void registrarCliente(String nombre, String email, String hash, String empresa) {
        jdbcTemplate.update("CALL sp_registrar_cliente(?, ?, ?, ?)", nombre, email, hash, empresa);
    }

    /** sp_crear_usuario: crea administrador o asesor (RF-014). */
    public void crearUsuario(String nombre, String email, String hash, Integer idRol, BigDecimal metaMensual) {
        jdbcTemplate.update("CALL sp_crear_usuario(?, ?, ?, ?, ?)", nombre, email, hash, idRol, metaMensual);
    }

    /** sp_cambiar_estado_usuario: activa o desactiva; protege al unico administrador (RF-014). */
    public void cambiarEstadoUsuario(Integer idUsuario, String estado) {
        jdbcTemplate.update("CALL sp_cambiar_estado_usuario(?, ?)", idUsuario, estado);
    }

    /** sp_asignar_rol: cambia el rol; no deja sin administrador al sistema (RF-015). */
    public void asignarRol(Integer idUsuario, Integer idRol) {
        jdbcTemplate.update("CALL sp_asignar_rol(?, ?)", idUsuario, idRol);
    }

    /** sp_registrar_ingreso: guarda el inicio de sesion (RF-004). */
    public void registrarIngreso(Integer idUsuario) {
        jdbcTemplate.update("CALL sp_registrar_ingreso(?)", idUsuario);
    }

    /** sp_registrar_actividad: guarda el evento y el trigger suma el puntaje (RF-005, RF-006). */
    public void registrarActividad(Integer idCliente, String tipoEvento, String url, Integer tiempoSeg) {
        jdbcTemplate.update("CALL sp_registrar_actividad(?, ?, ?, ?)", idCliente, tipoEvento, url, tiempoSeg);
    }

    /** sp_descargar_prueba: crea la prueba gratuita y registra el evento DESCARGA. */
    public void descargarPrueba(Integer idCliente, Integer dias) {
        jdbcTemplate.update("CALL sp_descargar_prueba(?, ?)", idCliente, dias);
    }

    /** sp_asignar_prospecto: asigna el prospecto a un asesor activo y le crea una alerta (RF-016). */
    public void asignarProspecto(Integer idCliente, Integer idAsesor) {
        jdbcTemplate.update("CALL sp_asignar_prospecto(?, ?)", idCliente, idAsesor);
    }

    /** sp_cambiar_estado_prospecto: solo el asesor asignado puede cambiarlo (RF-013). */
    public void cambiarEstadoProspecto(Integer idCliente, Integer idAsesor, String estado) {
        jdbcTemplate.update("CALL sp_cambiar_estado_prospecto(?, ?, ?)", idCliente, idAsesor, estado);
    }

    /** sp_registrar_seguimiento: guarda el contacto del asesor con su prospecto (RF-012). */
    public void registrarSeguimiento(Integer idCliente, Integer idAsesor, String medio, String observaciones) {
        jdbcTemplate.update("CALL sp_registrar_seguimiento(?, ?, ?, ?)", idCliente, idAsesor, medio, observaciones);
    }

    /** sp_marcar_alerta_leida: solo el destinatario puede marcarla (RF-011). */
    public void marcarAlertaLeida(Integer idAlerta, Integer idUsuario) {
        jdbcTemplate.update("CALL sp_marcar_alerta_leida(?, ?)", idAlerta, idUsuario);
    }

    /** sp_crear_propuesta: crea la propuesta y devuelve su id. */
    public Integer crearPropuesta(Integer idCliente, Integer idAsesor, BigDecimal monto) {
        return jdbcTemplate.queryForObject("CALL sp_crear_propuesta(?, ?, ?)", Integer.class,
                idCliente, idAsesor, monto);
    }

    /** sp_registrar_venta: crea la venta, acepta la propuesta y cierra el prospecto en una transaccion. */
    public void registrarVenta(Integer idPropuesta, BigDecimal montoFinal, String tipoLicencia, String periodo) {
        jdbcTemplate.update("CALL sp_registrar_venta(?, ?, ?, ?)", idPropuesta, montoFinal, tipoLicencia, periodo);
    }

    /** sp_solicitar_recuperacion: guarda el hash del token de recuperacion con su vencimiento (RF-003). */
    public void solicitarRecuperacion(String email, String tokenHash, Integer minutos) {
        jdbcTemplate.update("CALL sp_solicitar_recuperacion(?, ?, ?)", email, tokenHash, minutos);
    }

    /** sp_restablecer_contrasena: valida el token y guarda la nueva contrasena cifrada (RF-003). */
    public void restablecerContrasena(Integer idUsuario, String tokenHash, String nuevoHash) {
        jdbcTemplate.update("CALL sp_restablecer_contrasena(?, ?, ?)", idUsuario, tokenHash, nuevoHash);
    }
}
