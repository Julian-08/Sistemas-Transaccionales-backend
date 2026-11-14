package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.AsesorRs;
import com.uniminuto.zimbra.models.EstadisticasAsesorRs;
import com.uniminuto.zimbra.models.MetaAsesorRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.UsuarioSesion;

import java.util.List;

/**
 * Consulta de asesores, su meta mensual y el panel de estadisticas del asesor.
 */
public interface AsesorService {

    List<AsesorRs> listar(boolean soloActivos);

    RespuestaRs actualizarMeta(MetaAsesorRq metaRq);

    /** Panel del asesor de la sesion para el mes actual. */
    EstadisticasAsesorRs misEstadisticas(UsuarioSesion sesion);
}
