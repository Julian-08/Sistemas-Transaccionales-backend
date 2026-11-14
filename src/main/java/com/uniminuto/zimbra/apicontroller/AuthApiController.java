package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.AuthApi;
import com.uniminuto.zimbra.models.LoginRq;
import com.uniminuto.zimbra.models.LoginRs;
import com.uniminuto.zimbra.models.MenuOpcionRs;
import com.uniminuto.zimbra.models.RecuperacionRs;
import com.uniminuto.zimbra.models.RecuperarContrasenaRq;
import com.uniminuto.zimbra.models.RegistroClienteRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.RestablecerContrasenaRq;
import com.uniminuto.zimbra.security.SesionActual;
import com.uniminuto.zimbra.security.UsuarioSesion;
import com.uniminuto.zimbra.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthApiController implements AuthApi {

    private final AuthService authService;

    public AuthApiController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<LoginRs> login(LoginRq loginRq) {
        return ResponseEntity.ok(authService.login(loginRq));
    }

    @Override
    public ResponseEntity<RespuestaRs> registrarCliente(RegistroClienteRq registroRq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrarCliente(registroRq));
    }

    @Override
    public ResponseEntity<RecuperacionRs> solicitarRecuperacion(RecuperarContrasenaRq recuperarRq) {
        return ResponseEntity.ok(authService.solicitarRecuperacion(recuperarRq));
    }

    @Override
    public ResponseEntity<RespuestaRs> restablecerContrasena(RestablecerContrasenaRq restablecerRq) {
        return ResponseEntity.ok(authService.restablecerContrasena(restablecerRq));
    }

    @Override
    public ResponseEntity<RespuestaRs> cerrarSesion() {
        return ResponseEntity.ok(authService.cerrarSesion(SesionActual.claims()));
    }

    @Override
    public ResponseEntity<List<MenuOpcionRs>> obtenerMenu() {
        return ResponseEntity.ok(authService.obtenerMenu(SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<UsuarioSesion> obtenerSesion() {
        return ResponseEntity.ok(SesionActual.obtener());
    }
}
