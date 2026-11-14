package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.LoginRq;
import com.uniminuto.zimbra.models.LoginRs;
import com.uniminuto.zimbra.models.MenuOpcionRs;
import com.uniminuto.zimbra.models.RecuperacionRs;
import com.uniminuto.zimbra.models.RecuperarContrasenaRq;
import com.uniminuto.zimbra.models.RegistroClienteRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.RestablecerContrasenaRq;
import com.uniminuto.zimbra.security.Publico;
import com.uniminuto.zimbra.security.UsuarioSesion;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Autenticacion: inicio y cierre de sesion, registro de clientes y recuperacion
 * de contrasena (RF-001, RF-002, RF-003).
 */
@RequestMapping("/auth")
public interface AuthApi {

    /**
     * Inicia sesion con correo y contrasena (RF-001).
     *
     * @param loginRq correo y contrasena.
     * @return token JWT, datos del usuario y menu de su rol.
     */
    @Publico
    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    ResponseEntity<LoginRs> login(@Valid @RequestBody LoginRq loginRq);

    /**
     * Registro de un cliente desde el sitio web (para descargar la prueba).
     */
    @Publico
    @PostMapping(value = "/registro", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> registrarCliente(@Valid @RequestBody RegistroClienteRq registroRq);

    /**
     * Genera un codigo de recuperacion de 8 caracteres con vigencia limitada (RF-003).
     */
    @Publico
    @PostMapping(value = "/recuperar-contrasena", produces = "application/json", consumes = "application/json")
    ResponseEntity<RecuperacionRs> solicitarRecuperacion(@Valid @RequestBody RecuperarContrasenaRq recuperarRq);

    /**
     * Define la nueva contrasena con el correo y el codigo de recuperacion (RF-003).
     */
    @Publico
    @PostMapping(value = "/restablecer-contrasena", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> restablecerContrasena(@Valid @RequestBody RestablecerContrasenaRq restablecerRq);

    /**
     * Cierra la sesion actual; el token deja de servir (RF-001).
     */
    @PostMapping(value = "/cerrar-sesion", produces = "application/json")
    ResponseEntity<RespuestaRs> cerrarSesion();

    /**
     * Menu del usuario segun su rol (RF-002).
     */
    @GetMapping(value = "/menu", produces = "application/json")
    ResponseEntity<List<MenuOpcionRs>> obtenerMenu();

    /**
     * Datos del usuario de la sesion actual.
     */
    @GetMapping(value = "/sesion", produces = "application/json")
    ResponseEntity<UsuarioSesion> obtenerSesion();
}
