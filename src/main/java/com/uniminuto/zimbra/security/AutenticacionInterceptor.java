package com.uniminuto.zimbra.security;

import com.uniminuto.zimbra.entity.Usuario;
import com.uniminuto.zimbra.exception.AccesoDenegadoException;
import com.uniminuto.zimbra.exception.NoAutorizadoException;
import com.uniminuto.zimbra.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Objects;

/**
 * Revisa cada peticion antes de llegar al ApiController:
 * <ol>
 *   <li>Deja pasar los endpoints marcados con {@link Publico}.</li>
 *   <li>Exige el header {@code Authorization: Bearer <token>} y valida el token (RF-001).</li>
 *   <li>Rechaza tokens cerrados con "cerrar sesion" o de usuarios inactivos o con otro rol (RF-014, RF-015).</li>
 *   <li>Valida que el rol pueda usar el endpoint segun {@link RolesPermitidos} (RNF-002).</li>
 *   <li>Devuelve un token renovado en el header {@value #HEADER_TOKEN_RENOVADO}, asi la sesion
 *       solo vence por inactividad (RNF-003).</li>
 * </ol>
 */
@Component
public class AutenticacionInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_SESION = "zimbra.usuarioSesion";
    public static final String ATRIBUTO_CLAIMS = "zimbra.claims";
    public static final String HEADER_TOKEN_RENOVADO = "X-Token-Renovado";

    private static final String PREFIJO_BEARER = "Bearer ";
    private static final String ESTADO_ACTIVO = "ACTIVO";

    private final JwtService jwtService;
    private final TokenRevocadoStore tokenRevocadoStore;
    private final UsuarioRepository usuarioRepository;

    public AutenticacionInterceptor(JwtService jwtService,
                                    TokenRevocadoStore tokenRevocadoStore,
                                    UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.tokenRevocadoStore = tokenRevocadoStore;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod metodo) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (buscarAnotacion(metodo, Publico.class) != null) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(PREFIJO_BEARER)) {
            throw new NoAutorizadoException("Debe iniciar sesión para acceder a este recurso.");
        }

        Claims claims = jwtService.validar(header.substring(PREFIJO_BEARER.length()).trim());
        if (tokenRevocadoStore.estaRevocado(claims.getId())) {
            throw new NoAutorizadoException("La sesión fue cerrada. Inicie sesión nuevamente.");
        }

        UsuarioSesion sesion = jwtService.aSesion(claims);
        Usuario usuario = usuarioRepository.findById(sesion.idUsuario())
                .orElseThrow(() -> new NoAutorizadoException("Su sesión ya no es válida. Inicie sesión nuevamente."));
        if (!ESTADO_ACTIVO.equals(usuario.getEstado())
                || !Objects.equals(Roles.nombrePorId(usuario.getIdRol()), sesion.rol())) {
            throw new NoAutorizadoException("Su sesión ya no es válida. Inicie sesión nuevamente.");
        }

        RolesPermitidos permitidos = buscarAnotacion(metodo, RolesPermitidos.class);
        if (permitidos != null && Arrays.stream(permitidos.value()).noneMatch(sesion.rol()::equals)) {
            throw new AccesoDenegadoException("No tiene permisos para acceder a esta opción.");
        }

        request.setAttribute(ATRIBUTO_SESION, sesion);
        request.setAttribute(ATRIBUTO_CLAIMS, claims);
        response.setHeader(HEADER_TOKEN_RENOVADO, jwtService.generarToken(sesion, claims.getId()));
        return true;
    }

    /** Busca la anotacion en el metodo (incluida la interfaz Api) y si no, en la clase. */
    private <A extends java.lang.annotation.Annotation> A buscarAnotacion(HandlerMethod metodo, Class<A> tipo) {
        A anotacion = AnnotatedElementUtils.findMergedAnnotation(metodo.getMethod(), tipo);
        if (anotacion == null) {
            anotacion = AnnotatedElementUtils.findMergedAnnotation(metodo.getBeanType(), tipo);
        }
        return anotacion;
    }
}
