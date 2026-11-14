package com.uniminuto.zimbra.service;

/**
 * Registro en segundo plano de ingresos y navegacion (RF-004, RF-005, RNF-006).
 * Si el guardado falla, se reintenta sin afectar la navegacion del cliente.
 */
public interface RegistroActividadService {

    void registrarIngreso(Integer idUsuario);

    void registrarActividad(Integer idCliente, String tipoEvento, String url, Integer tiempoSeg);
}
