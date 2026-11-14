package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.entity.Rol;
import com.uniminuto.zimbra.entity.Usuario;
import com.uniminuto.zimbra.exception.BadRequestException;
import com.uniminuto.zimbra.exception.RecursoNoEncontradoException;
import com.uniminuto.zimbra.models.AsignarRolRq;
import com.uniminuto.zimbra.models.CambiarEstadoUsuarioRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.UsuarioActualizarRq;
import com.uniminuto.zimbra.models.UsuarioRq;
import com.uniminuto.zimbra.models.UsuarioRs;
import com.uniminuto.zimbra.repository.ClienteRepository;
import com.uniminuto.zimbra.repository.ConsultaRepository;
import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.repository.RolRepository;
import com.uniminuto.zimbra.repository.UsuarioRepository;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.service.UsuarioService;
import com.uniminuto.zimbra.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final RolRepository rolRepository;
    private final ConsultaRepository consultaRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final PasswordUtil passwordUtil;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              ClienteRepository clienteRepository,
                              RolRepository rolRepository,
                              ConsultaRepository consultaRepository,
                              ProcedimientoRepository procedimientoRepository,
                              PasswordUtil passwordUtil) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.rolRepository = rolRepository;
        this.consultaRepository = consultaRepository;
        this.procedimientoRepository = procedimientoRepository;
        this.passwordUtil = passwordUtil;
    }

    @Override
    public List<UsuarioRs> listar(Integer idRol) {
        return consultaRepository.listarUsuarios(idRol);
    }

    @Override
    public UsuarioRs obtener(Integer idUsuario) {
        return consultaRepository.obtenerUsuario(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe."));
    }

    @Override
    public RespuestaRs crear(UsuarioRq usuarioRq) {
        if (usuarioRq.getIdRol() != Roles.ID_ADMINISTRADOR && usuarioRq.getIdRol() != Roles.ID_ASESOR) {
            throw new BadRequestException(
                    "Aquí solo se crean administradores (1) o asesores (2); los clientes se registran desde el sitio web.");
        }
        passwordUtil.validarFortaleza(usuarioRq.getContrasena());
        String email = normalizarEmail(usuarioRq.getEmail());
        BigDecimal meta = usuarioRq.getMetaMensual() == null ? BigDecimal.ZERO : usuarioRq.getMetaMensual();

        // sp_crear_usuario valida el correo duplicado (RF-014 A1) y crea la ficha del asesor.
        procedimientoRepository.crearUsuario(usuarioRq.getNombre().trim(), email,
                passwordUtil.cifrar(usuarioRq.getContrasena()), usuarioRq.getIdRol(), meta);

        Integer idUsuario = usuarioRepository.findByEmail(email).map(Usuario::getIdUsuario).orElse(null);
        return RespuestaRs.ok("Usuario creado correctamente.", idUsuario);
    }

    @Override
    public RespuestaRs actualizar(UsuarioActualizarRq actualizarRq) {
        Integer idUsuario = actualizarRq.getIdUsuario();
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RecursoNoEncontradoException("El usuario no existe.");
        }
        String email = normalizarEmail(actualizarRq.getEmail());
        if (usuarioRepository.existsByEmailAndIdUsuarioNot(email, idUsuario)) {
            throw new BadRequestException("El correo ya está registrado.");
        }
        usuarioRepository.actualizarDatos(idUsuario, actualizarRq.getNombre().trim(), email);

        String nuevaContrasena = actualizarRq.getContrasena();
        if (nuevaContrasena != null && !nuevaContrasena.isBlank()) {
            passwordUtil.validarFortaleza(nuevaContrasena);
            usuarioRepository.actualizarContrasena(idUsuario, passwordUtil.cifrar(nuevaContrasena));
        }
        return RespuestaRs.ok("Usuario actualizado correctamente.", idUsuario);
    }

    @Override
    public RespuestaRs cambiarEstado(CambiarEstadoUsuarioRq estadoRq) {
        if (!usuarioRepository.existsById(estadoRq.getIdUsuario())) {
            throw new RecursoNoEncontradoException("El usuario no existe.");
        }
        // sp_cambiar_estado_usuario no deja desactivar al unico administrador activo.
        procedimientoRepository.cambiarEstadoUsuario(estadoRq.getIdUsuario(), estadoRq.getEstado());
        String accion = "ACTIVO".equals(estadoRq.getEstado()) ? "activado" : "desactivado";
        return RespuestaRs.ok("Usuario " + accion + " correctamente.", estadoRq.getIdUsuario());
    }

    @Override
    public RespuestaRs asignarRol(AsignarRolRq asignarRolRq) {
        Integer idUsuario = asignarRolRq.getIdUsuario();
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RecursoNoEncontradoException("El usuario no existe.");
        }
        if (asignarRolRq.getIdRol() == Roles.ID_CLIENTE && clienteRepository.findByIdUsuario(idUsuario).isEmpty()) {
            throw new BadRequestException(
                    "Solo un usuario registrado como cliente desde el sitio web puede tener el rol CLIENTE.");
        }
        // sp_asignar_rol no deja sin administrador al sistema (RF-015 A1).
        procedimientoRepository.asignarRol(idUsuario, asignarRolRq.getIdRol());
        return RespuestaRs.ok("Rol asignado. Se aplica en el próximo inicio de sesión del usuario.", idUsuario);
    }

    @Override
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
