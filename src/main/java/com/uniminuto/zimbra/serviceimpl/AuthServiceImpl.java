package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.entity.AsesorComercial;
import com.uniminuto.zimbra.entity.Cliente;
import com.uniminuto.zimbra.entity.Usuario;
import com.uniminuto.zimbra.exception.AccesoDenegadoException;
import com.uniminuto.zimbra.exception.BadRequestException;
import com.uniminuto.zimbra.models.RecuperacionRs;
import org.springframework.dao.DataAccessException;
import java.security.SecureRandom;
import com.uniminuto.zimbra.exception.NoAutorizadoException;
import com.uniminuto.zimbra.models.LoginRq;
import com.uniminuto.zimbra.models.LoginRs;
import com.uniminuto.zimbra.models.MenuOpcionRs;
import com.uniminuto.zimbra.models.RecuperarContrasenaRq;
import com.uniminuto.zimbra.models.RegistroClienteRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.RestablecerContrasenaRq;
import com.uniminuto.zimbra.repository.AsesorComercialRepository;
import com.uniminuto.zimbra.repository.ClienteRepository;
import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.repository.UsuarioRepository;
import com.uniminuto.zimbra.security.JwtService;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.TokenRevocadoStore;
import com.uniminuto.zimbra.security.UsuarioSesion;
import com.uniminuto.zimbra.service.AuthService;
import com.uniminuto.zimbra.service.EmailService;
import com.uniminuto.zimbra.service.MenuService;
import com.uniminuto.zimbra.service.RegistroActividadService;
import com.uniminuto.zimbra.util.PasswordUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String MENSAJE_CREDENCIALES = "Correo o contraseña incorrectos.";
    private static final String MENSAJE_RECUPERACION =
            "Si el correo está registrado, recibirá un código para restablecer su contraseña.";
    private static final String MENSAJE_CODIGO_INVALIDO = "El correo o el código no son correctos, o el código ya se usó.";
    private static final String ALFABETO_CODIGO = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD_CODIGO = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final AsesorComercialRepository asesorRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final RegistroActividadService registroActividadService;
    private final JwtService jwtService;
    private final TokenRevocadoStore tokenRevocadoStore;
    private final PasswordUtil passwordUtil;
    private final MenuService menuService;
    private final EmailService emailService;
    private final int minutosRecuperacion;
    /** Solo en desarrollo: devuelve el codigo en la respuesta para no depender del correo. */
    private final boolean mostrarCodigo;

    /**
     * Hash BCrypt de relleno: si el correo no existe se compara contra este hash
     * para que la respuesta tarde lo mismo y no revele que cuentas existen.
     */
    private final String hashRelleno;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           ClienteRepository clienteRepository,
                           AsesorComercialRepository asesorRepository,
                           ProcedimientoRepository procedimientoRepository,
                           RegistroActividadService registroActividadService,
                           JwtService jwtService,
                           TokenRevocadoStore tokenRevocadoStore,
                           PasswordUtil passwordUtil,
                           MenuService menuService,
                           EmailService emailService,
                           @Value("${zimbra.recuperacion.minutos-vigencia:15}") int minutosRecuperacion,
                           @Value("${zimbra.recuperacion.mostrar-codigo:false}") boolean mostrarCodigo) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.asesorRepository = asesorRepository;
        this.procedimientoRepository = procedimientoRepository;
        this.registroActividadService = registroActividadService;
        this.jwtService = jwtService;
        this.tokenRevocadoStore = tokenRevocadoStore;
        this.passwordUtil = passwordUtil;
        this.menuService = menuService;
        this.emailService = emailService;
        this.minutosRecuperacion = minutosRecuperacion;
        this.mostrarCodigo = mostrarCodigo;
        if (mostrarCodigo) {
            LOG.warn("zimbra.recuperacion.mostrar-codigo=true: el código de recuperación se devuelve en la respuesta. "
                    + "Úselo solo en desarrollo.");
        }
        this.hashRelleno = passwordUtil.cifrar(UUID.randomUUID().toString());
    }

    @Override
    public LoginRs login(LoginRq loginRq) {
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(normalizarEmail(loginRq.getEmail()));
        String hashGuardado = optUsuario
                .flatMap(u -> usuarioRepository.buscarContrasenaHash(u.getIdUsuario()))
                .orElse(hashRelleno);
        boolean contrasenaValida = passwordUtil.coincide(loginRq.getContrasena(), hashGuardado);

        // RF-001 A1: mismo mensaje si el correo no existe o la contrasena no coincide.
        if (optUsuario.isEmpty() || !contrasenaValida) {
            throw new NoAutorizadoException(MENSAJE_CREDENCIALES);
        }

        Usuario usuario = optUsuario.get();
        // RF-001 A2 / RF-014 A2: un usuario inactivo no puede ingresar.
        if (!ESTADO_ACTIVO.equals(usuario.getEstado())) {
            throw new AccesoDenegadoException("Su usuario está inactivo. Contacte al administrador.");
        }

        // RF-002 A1: sin un rol valido no hay acceso.
        String rol = Roles.nombrePorId(usuario.getIdRol());
        if (rol == null) {
            LOG.warn("El usuario {} intentó ingresar sin un rol válido (id_rol={}).",
                    usuario.getIdUsuario(), usuario.getIdRol());
            throw new AccesoDenegadoException(
                    "Su usuario no tiene un rol asignado. Contacte al administrador.");
        }

        Integer idAsesor = Roles.ASESOR.equals(rol)
                ? asesorRepository.findByIdUsuario(usuario.getIdUsuario())
                    .map(AsesorComercial::getIdAsesor).orElse(null)
                : null;
        Integer idCliente = Roles.CLIENTE.equals(rol)
                ? clienteRepository.findByIdUsuario(usuario.getIdUsuario())
                    .map(Cliente::getIdCliente).orElse(null)
                : null;

        UsuarioSesion sesion = new UsuarioSesion(usuario.getIdUsuario(), usuario.getNombre(),
                usuario.getEmail(), rol, idAsesor, idCliente);

        // RF-004: registra el ingreso en segundo plano (RNF-006).
        registroActividadService.registrarIngreso(usuario.getIdUsuario());
        // RF-006 / RF-008: el ingreso del cliente cuenta como visita; suma puntaje y,
        // si ya es prospecto, el trigger crea la alerta para su asesor.
        if (idCliente != null) {
            registroActividadService.registrarActividad(idCliente, "VISITA_WEB", "/login", 0);
        }

        LoginRs loginRs = new LoginRs();
        loginRs.setToken(jwtService.generarToken(sesion));
        loginRs.setMinutosInactividad(jwtService.getMinutosInactividad());
        loginRs.setIdUsuario(usuario.getIdUsuario());
        loginRs.setNombre(usuario.getNombre());
        loginRs.setEmail(usuario.getEmail());
        loginRs.setRol(rol);
        loginRs.setMenu(menuService.menuPorRol(rol));
        return loginRs;
    }

    @Override
    public RespuestaRs cerrarSesion(Claims claims) {
        // Ningun token de esta sesion puede durar mas que los minutos de inactividad desde ahora.
        Instant limite = Instant.now().plus(jwtService.getMinutosInactividad() + 1, ChronoUnit.MINUTES);
        tokenRevocadoStore.revocar(claims.getId(), limite);
        return RespuestaRs.ok("Sesión cerrada correctamente.");
    }

    @Override
    public RespuestaRs registrarCliente(RegistroClienteRq registroRq) {
        passwordUtil.validarFortaleza(registroRq.getContrasena());
        String email = normalizarEmail(registroRq.getEmail());
        procedimientoRepository.registrarCliente(
                registroRq.getNombre().trim(),
                email,
                passwordUtil.cifrar(registroRq.getContrasena()),
                registroRq.getEmpresa().trim());
        Integer idUsuario = usuarioRepository.findByEmail(email).map(Usuario::getIdUsuario).orElse(null);
        emailService.enviarBienvenida(email, registroRq.getNombre().trim(), registroRq.getEmpresa().trim());
        return RespuestaRs.ok("Registro exitoso. Ya puede iniciar sesión.", idUsuario);
    }

    /**
     * Genera un codigo de 8 caracteres que dura {@code zimbra.recuperacion.minutos-vigencia} minutos.
     * En la base solo se guarda su hash SHA-256 (usuarios.token_recuperacion_hash) y el vencimiento
     * (usuarios.token_expiracion), con sp_solicitar_recuperacion. Un codigo nuevo reemplaza al anterior.
     */
    @Override
    public RecuperacionRs solicitarRecuperacion(RecuperarContrasenaRq recuperarRq) {
        String email = normalizarEmail(recuperarRq.getEmail());
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(email)
                .filter(u -> ESTADO_ACTIVO.equals(u.getEstado()));

        if (optUsuario.isEmpty()) {
            // RF-003 A1: mensaje generico, no revela si la cuenta existe.
            return new RecuperacionRs(Boolean.TRUE, MENSAJE_RECUPERACION, null, null);
        }

        Usuario usuario = optUsuario.get();
        String codigo = generarCodigo();
        procedimientoRepository.solicitarRecuperacion(email, passwordUtil.hashToken(codigo), minutosRecuperacion);
        emailService.enviarCodigoRecuperacion(email, usuario.getNombre(), formatearCodigo(codigo), minutosRecuperacion);

        if (mostrarCodigo) {
            return new RecuperacionRs(Boolean.TRUE,
                    "Código generado. Úselo antes de " + minutosRecuperacion + " minutos para restablecer la contraseña.",
                    formatearCodigo(codigo), minutosRecuperacion);
        }
        return new RecuperacionRs(Boolean.TRUE, MENSAJE_RECUPERACION, null, minutosRecuperacion);
    }

    @Override
    public RespuestaRs restablecerContrasena(RestablecerContrasenaRq restablecerRq) {
        passwordUtil.validarFortaleza(restablecerRq.getNuevaContrasena());
        String codigo = limpiarCodigo(restablecerRq.getCodigo());
        Integer idUsuario = usuarioRepository.findByEmail(normalizarEmail(restablecerRq.getEmail()))
                .map(Usuario::getIdUsuario)
                .orElseThrow(() -> new BadRequestException(MENSAJE_CODIGO_INVALIDO));
        try {
            procedimientoRepository.restablecerContrasena(
                    idUsuario,
                    passwordUtil.hashToken(codigo),
                    passwordUtil.cifrar(restablecerRq.getNuevaContrasena()));
        } catch (DataAccessException ex) {
            // sp_restablecer_contrasena habla de "enlace"; aqui se traduce a "codigo".
            String mensaje = String.valueOf(ex.getMostSpecificCause().getMessage());
            if (mensaje.contains("venció")) {
                throw new BadRequestException("El código venció. Solicite uno nuevo.");
            }
            if (mensaje.contains("inválido")) {
                throw new BadRequestException(MENSAJE_CODIGO_INVALIDO);
            }
            throw ex;
        }
        return RespuestaRs.ok("Su contraseña se actualizó. Ya puede iniciar sesión.");
    }

    /** 8 caracteres sin letras ni numeros que se confundan (0/O, 1/I/L). */
    private String generarCodigo() {
        StringBuilder codigo = new StringBuilder(LONGITUD_CODIGO);
        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            codigo.append(ALFABETO_CODIGO.charAt(RANDOM.nextInt(ALFABETO_CODIGO.length())));
        }
        return codigo.toString();
    }

    /** ABCD2345 -> ABCD-2345, para leerlo mas facil. */
    private String formatearCodigo(String codigo) {
        return codigo.substring(0, 4) + "-" + codigo.substring(4);
    }

    /** Acepta el codigo con o sin guion, espacios o minusculas. */
    private String limpiarCodigo(String codigo) {
        return codigo == null ? "" : codigo.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
    }

    @Override
    public List<MenuOpcionRs> obtenerMenu(UsuarioSesion sesion) {
        return menuService.menuPorRol(sesion.rol());
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
