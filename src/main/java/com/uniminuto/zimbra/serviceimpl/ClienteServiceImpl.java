package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.entity.Cliente;
import com.uniminuto.zimbra.entity.PruebaDescarga;
import com.uniminuto.zimbra.entity.Usuario;
import com.uniminuto.zimbra.exception.RecursoNoEncontradoException;
import com.uniminuto.zimbra.models.ActividadRq;
import com.uniminuto.zimbra.models.PerfilClienteRs;
import com.uniminuto.zimbra.models.PruebaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.repository.ClienteRepository;
import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.repository.PruebaDescargaRepository;
import com.uniminuto.zimbra.repository.UsuarioRepository;
import com.uniminuto.zimbra.security.UsuarioSesion;
import com.uniminuto.zimbra.security.ValidadorSesion;
import com.uniminuto.zimbra.service.ClienteService;
import com.uniminuto.zimbra.service.ConfiguracionService;
import com.uniminuto.zimbra.service.RegistroActividadService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PruebaDescargaRepository pruebaRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final RegistroActividadService registroActividadService;
    private final ConfiguracionService configuracionService;

    public ClienteServiceImpl(ClienteRepository clienteRepository,
                              UsuarioRepository usuarioRepository,
                              PruebaDescargaRepository pruebaRepository,
                              ProcedimientoRepository procedimientoRepository,
                              RegistroActividadService registroActividadService,
                              ConfiguracionService configuracionService) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pruebaRepository = pruebaRepository;
        this.procedimientoRepository = procedimientoRepository;
        this.registroActividadService = registroActividadService;
        this.configuracionService = configuracionService;
    }

    @Override
    public PerfilClienteRs obtenerPerfil(UsuarioSesion sesion) {
        Integer idCliente = ValidadorSesion.idCliente(sesion);
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("El cliente no existe."));
        Usuario usuario = usuarioRepository.findById(sesion.idUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe."));

        // RNF-004: el perfil del cliente no incluye puntaje ni estado de prospecto.
        PerfilClienteRs perfil = new PerfilClienteRs();
        perfil.setIdCliente(cliente.getIdCliente());
        perfil.setNombre(usuario.getNombre());
        perfil.setEmail(usuario.getEmail());
        perfil.setEmpresa(cliente.getEmpresa());
        perfil.setFechaRegistro(cliente.getFechaRegistro());
        perfil.setPruebaVigente(listarPruebas(sesion).stream()
                .filter(PruebaRs::getVigente)
                .findFirst()
                .orElse(null));
        return perfil;
    }

    @Override
    public RespuestaRs descargarPrueba(UsuarioSesion sesion) {
        Integer idCliente = ValidadorSesion.idCliente(sesion);
        int dias = configuracionService.obtenerDiasPrueba();
        // sp_descargar_prueba evita dos pruebas vigentes y registra el evento DESCARGA (suma puntaje).
        procedimientoRepository.descargarPrueba(idCliente, dias);
        return RespuestaRs.ok("Su prueba gratuita de Zimbra por " + dias + " días está lista para descargar.");
    }

    @Override
    public List<PruebaRs> listarPruebas(UsuarioSesion sesion) {
        Integer idCliente = ValidadorSesion.idCliente(sesion);
        return pruebaRepository.findByIdClienteOrderByFechaInicioDesc(idCliente).stream()
                .map(this::aPruebaRs)
                .toList();
    }

    @Override
    public void registrarActividad(ActividadRq actividadRq, UsuarioSesion sesion) {
        Integer idCliente = ValidadorSesion.idCliente(sesion);
        String url = actividadRq.getUrlVisitada() == null || actividadRq.getUrlVisitada().isBlank()
                ? "/" : actividadRq.getUrlVisitada().trim();
        registroActividadService.registrarActividad(idCliente, actividadRq.getTipoEvento(), url,
                actividadRq.getTiempoPermanenciaSeg());
    }

    private PruebaRs aPruebaRs(PruebaDescarga prueba) {
        LocalDateTime ahora = LocalDateTime.now();
        boolean vigente = prueba.getFechaVencimiento() != null && prueba.getFechaVencimiento().isAfter(ahora);
        PruebaRs rs = new PruebaRs();
        rs.setIdPrueba(prueba.getIdPrueba());
        rs.setFechaInicio(prueba.getFechaInicio());
        rs.setDiasPrueba(prueba.getDiasPrueba());
        rs.setFechaVencimiento(prueba.getFechaVencimiento());
        rs.setVigente(vigente);
        rs.setDiasRestantes(vigente ? ChronoUnit.DAYS.between(ahora, prueba.getFechaVencimiento()) : 0L);
        return rs;
    }
}
