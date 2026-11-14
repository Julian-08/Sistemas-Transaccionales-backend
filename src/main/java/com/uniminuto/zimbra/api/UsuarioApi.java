package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.entity.Rol;
import com.uniminuto.zimbra.models.AsignarRolRq;
import com.uniminuto.zimbra.models.CambiarEstadoUsuarioRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.UsuarioActualizarRq;
import com.uniminuto.zimbra.models.UsuarioRq;
import com.uniminuto.zimbra.models.UsuarioRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Gestion de usuarios y roles. Solo administrador (RF-014, RF-015).
 */
@RequestMapping("/usuario")
@RolesPermitidos({Roles.ADMINISTRADOR})
public interface UsuarioApi {

    /**
     * Lista los usuarios ordenados por nombre.
     *
     * @param idRol opcional: filtra por rol (1 administrador, 2 asesor, 3 cliente).
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<UsuarioRs>> listar(@RequestParam(value = "idRol", required = false) Integer idRol);

    /**
     * Consulta un usuario por id.
     */
    @GetMapping(value = "/{idUsuario}", produces = "application/json")
    ResponseEntity<UsuarioRs> obtener(@PathVariable("idUsuario") Integer idUsuario);

    /**
     * Crea un administrador o un asesor; la contrasena se guarda cifrada (RF-014, RNF-001).
     */
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> guardar(@Valid @RequestBody UsuarioRq usuarioRq);

    /**
     * Edita nombre, correo y opcionalmente la contrasena (RF-014).
     */
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> actualizar(@Valid @RequestBody UsuarioActualizarRq actualizarRq);

    /**
     * Activa o desactiva un usuario (RF-014).
     */
    @PutMapping(value = "/cambiar-estado", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> cambiarEstado(@Valid @RequestBody CambiarEstadoUsuarioRq estadoRq);

    /**
     * Asigna el rol de un usuario (RF-015).
     */
    @PutMapping(value = "/asignar-rol", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> asignarRol(@Valid @RequestBody AsignarRolRq asignarRolRq);

    /**
     * Lista los roles disponibles.
     */
    @GetMapping(value = "/roles", produces = "application/json")
    ResponseEntity<List<Rol>> listarRoles();
}
