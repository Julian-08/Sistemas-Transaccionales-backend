package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.entity.Configuracion;
import com.uniminuto.zimbra.models.ConfiguracionRq;
import com.uniminuto.zimbra.models.PuntajeMinimoRq;
import com.uniminuto.zimbra.models.RespuestaRs;

import java.util.List;

/**
 * Parametros del sistema (RF-017 y reglas de clientes calificados e inactivos).
 */
public interface ConfiguracionService {

    List<Configuracion> listar();

    int obtenerPuntajeMinimo();

    RespuestaRs actualizarPuntajeMinimo(PuntajeMinimoRq puntajeRq);

    /** Cambia un parametro numerico conocido. */
    RespuestaRs actualizar(ConfiguracionRq configuracionRq);

    int obtenerDiasPrueba();

    int obtenerIngresosCalificado();

    int obtenerDiasCalificado();

    int obtenerDiasInactividad();
}
