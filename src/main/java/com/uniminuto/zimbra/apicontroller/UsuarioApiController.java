package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.UsuarioApi;
import com.uniminuto.zimbra.entity.Rol;
import com.uniminuto.zimbra.models.AsignarRolRq;
import com.uniminuto.zimbra.models.CambiarEstadoUsuarioRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.UsuarioActualizarRq;
import com.uniminuto.zimbra.models.UsuarioRq;
import com.uniminuto.zimbra.models.UsuarioRs;
import com.uniminuto.zimbra.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsuarioApiController implements UsuarioApi {

    private final UsuarioService usuarioService;

    public UsuarioApiController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public ResponseEntity<List<UsuarioRs>> listar(Integer idRol) {
        return ResponseEntity.ok(usuarioService.listar(idRol));
    }

    @Override
    public ResponseEntity<UsuarioRs> obtener(Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.obtener(idUsuario));
    }

    @Override
    public ResponseEntity<RespuestaRs> guardar(UsuarioRq usuarioRq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(usuarioRq));
    }

    @Override
    public ResponseEntity<RespuestaRs> actualizar(UsuarioActualizarRq actualizarRq) {
        return ResponseEntity.ok(usuarioService.actualizar(actualizarRq));
    }

    @Override
    public ResponseEntity<RespuestaRs> cambiarEstado(CambiarEstadoUsuarioRq estadoRq) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(estadoRq));
    }

    @Override
    public ResponseEntity<RespuestaRs> asignarRol(AsignarRolRq asignarRolRq) {
        return ResponseEntity.ok(usuarioService.asignarRol(asignarRolRq));
    }

    @Override
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(usuarioService.listarRoles());
    }
}
