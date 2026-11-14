package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.ActividadProspectoRs;
import com.uniminuto.zimbra.models.AsignarProspectoRq;
import com.uniminuto.zimbra.models.CambiarEstadoProspectoRq;
import com.uniminuto.zimbra.models.ClienteCalificadoRs;
import com.uniminuto.zimbra.models.ProspectoRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Prospectos: listado, actividad, cambio de estado y asignacion
 * (RF-009, RF-010, RF-013, RF-016).
 */
@RequestMapping("/prospecto")
public interface ProspectoApi {

    /**
     * Prospectos asignados al asesor de la sesion, con puntaje y estado (RF-009).
     *
     * @param estado opcional: NUEVO, CONTACTADO, INTERESADO, CERRADO o DESCARTADO.
     */
    @RolesPermitidos({Roles.ASESOR})
    @GetMapping(value = "/mis-prospectos", produces = "application/json")
    ResponseEntity<List<ProspectoRs>> listarMisProspectos(
            @RequestParam(value = "estado", required = false) String estado);

    /**
     * Todos los prospectos para el administrador (RF-016).
     *
     * @param idAsesor   opcional: solo los de este asesor.
     * @param sinAsignar true = solo los que no tienen asesor.
     * @param estado     opcional: estado del prospecto.
     */
    @RolesPermitidos({Roles.ADMINISTRADOR})
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<ProspectoRs>> listarTodos(
            @RequestParam(value = "idAsesor", required = false) Integer idAsesor,
            @RequestParam(value = "sinAsignar", defaultValue = "false") boolean sinAsignar,
            @RequestParam(value = "estado", required = false) String estado);

    /**
     * Visitas, paginas visitadas, puntaje y seguimientos de un prospecto (RF-010).
     * El asesor solo puede consultar sus propios prospectos.
     */
    @RolesPermitidos({Roles.ASESOR, Roles.ADMINISTRADOR})
    @GetMapping(value = "/{idCliente}/actividad", produces = "application/json")
    ResponseEntity<ActividadProspectoRs> consultarActividad(@PathVariable("idCliente") Integer idCliente);

    /**
     * Cambia el estado de un prospecto asignado al asesor (RF-013).
     */
    @RolesPermitidos({Roles.ASESOR})
    @PutMapping(value = "/cambiar-estado", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> cambiarEstado(@Valid @RequestBody CambiarEstadoProspectoRq estadoRq);

    /**
     * Clientes listos para el plan de pago: INGRESOS_CALIFICADO ingresos o mas en los
     * ultimos DIAS_CALIFICADO dias (por defecto 50 en 60). El asesor ve solo los suyos.
     *
     * @param sinAsignar solo administrador: true = solo los que no tienen asesor.
     */
    @RolesPermitidos({Roles.ASESOR, Roles.ADMINISTRADOR})
    @GetMapping(value = "/calificados", produces = "application/json")
    ResponseEntity<List<ClienteCalificadoRs>> listarCalificados(
            @RequestParam(value = "sinAsignar", defaultValue = "false") boolean sinAsignar);

    /**
     * Asigna o reasigna un prospecto a un asesor activo (RF-016).
     */
    @RolesPermitidos({Roles.ADMINISTRADOR})
    @PutMapping(value = "/asignar", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> asignar(@Valid @RequestBody AsignarProspectoRq asignarRq);
}
