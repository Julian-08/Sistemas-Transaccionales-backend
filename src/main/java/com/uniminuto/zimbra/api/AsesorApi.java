package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.AsesorRs;
import com.uniminuto.zimbra.models.EstadisticasAsesorRs;
import com.uniminuto.zimbra.models.MetaAsesorRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Asesores comerciales. Solo administrador.
 */
@RequestMapping("/asesor")
@RolesPermitidos({Roles.ADMINISTRADOR})
public interface AsesorApi {

    /**
     * Lista los asesores con su meta y prospectos asignados.
     *
     * @param soloActivos true = solo asesores activos (para asignar prospectos, RF-016).
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<AsesorRs>> listar(
            @RequestParam(value = "soloActivos", defaultValue = "false") boolean soloActivos);

    /**
     * Panel del asesor de la sesion: prospectos por estado, calificados, alertas,
     * seguimientos, propuestas abiertas y ventas del mes frente a su meta.
     */
    @RolesPermitidos({Roles.ASESOR})
    @GetMapping(value = "/mis-estadisticas", produces = "application/json")
    ResponseEntity<EstadisticasAsesorRs> misEstadisticas();

    /**
     * Cambia la meta mensual de ventas de un asesor.
     */
    @PutMapping(value = "/meta", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> actualizarMeta(@Valid @RequestBody MetaAsesorRq metaRq);
}
