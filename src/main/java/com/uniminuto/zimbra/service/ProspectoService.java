package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.ActividadProspectoRs;
import com.uniminuto.zimbra.models.AsignarProspectoRq;
import com.uniminuto.zimbra.models.CambiarEstadoProspectoRq;
import com.uniminuto.zimbra.models.ClienteCalificadoRs;
import com.uniminuto.zimbra.models.ProspectoRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.UsuarioSesion;

import java.util.List;

/**
 * Gestion de prospectos (RF-009, RF-010, RF-013, RF-016).
 */
public interface ProspectoService {

    /** RF-009: prospectos asignados al asesor de la sesion. */
    List<ProspectoRs> listarMisProspectos(UsuarioSesion sesion, String estado);

    /** RF-016: todos los prospectos para el administrador. */
    List<ProspectoRs> listarTodos(Integer idAsesor, boolean sinAsignar, String estado);

    /** RF-010: visitas, paginas, puntaje y seguimientos de un prospecto. */
    ActividadProspectoRs consultarActividad(Integer idCliente, UsuarioSesion sesion);

    /** RF-013: cambio de estado por el asesor asignado. */
    RespuestaRs cambiarEstado(CambiarEstadoProspectoRq estadoRq, UsuarioSesion sesion);

    /** RF-016: asignar o reasignar un prospecto. */
    RespuestaRs asignar(AsignarProspectoRq asignarRq);

    /**
     * Clientes listos para el plan de pago (ingresos en la ventana configurada).
     * El asesor solo ve los suyos; el administrador ve todos.
     */
    List<ClienteCalificadoRs> listarCalificados(UsuarioSesion sesion, boolean sinAsignar);
}
