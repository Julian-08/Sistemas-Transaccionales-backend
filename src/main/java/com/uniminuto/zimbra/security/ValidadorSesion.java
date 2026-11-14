package com.uniminuto.zimbra.security;

import com.uniminuto.zimbra.exception.AccesoDenegadoException;

import java.util.Objects;

/**
 * Reglas de acceso a los datos de cada rol (RNF-002, RNF-004).
 * El rol del endpoint ya lo valida el interceptor; aqui se valida que el
 * usuario solo vea o cambie sus propios datos.
 */
public final class ValidadorSesion {

    private ValidadorSesion() {
    }

    /** Devuelve el id de asesor de la sesion o lanza 403 si no es asesor. */
    public static Integer idAsesor(UsuarioSesion sesion) {
        if (!sesion.esAsesor() || sesion.idAsesor() == null) {
            throw new AccesoDenegadoException("Esta opción es solo para asesores comerciales.");
        }
        return sesion.idAsesor();
    }

    /** Devuelve el id de cliente de la sesion o lanza 403 si no es cliente. */
    public static Integer idCliente(UsuarioSesion sesion) {
        if (!sesion.esCliente() || sesion.idCliente() == null) {
            throw new AccesoDenegadoException("Esta opción es solo para clientes.");
        }
        return sesion.idCliente();
    }

    /**
     * El administrador ve todo; el asesor solo lo que tiene asignado (RF-009, RF-010).
     */
    public static void validarAccesoProspecto(UsuarioSesion sesion, Integer idAsesorAsignado) {
        if (sesion.esAdministrador()) {
            return;
        }
        if (!sesion.esAsesor() || !Objects.equals(sesion.idAsesor(), idAsesorAsignado)) {
            throw new AccesoDenegadoException("Solo puede consultar los prospectos que tiene asignados.");
        }
    }
}
