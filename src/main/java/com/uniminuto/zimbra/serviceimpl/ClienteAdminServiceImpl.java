package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.exception.BadRequestException;
import com.uniminuto.zimbra.exception.RecursoNoEncontradoException;
import com.uniminuto.zimbra.models.ClienteAdminRs;
import com.uniminuto.zimbra.models.RegistroClienteRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.repository.ConsultaRepository;
import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.service.AuthService;
import com.uniminuto.zimbra.service.ClienteAdminService;
import com.uniminuto.zimbra.service.ConfiguracionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteAdminServiceImpl implements ClienteAdminService {

    private static final String ACTIVO = "ACTIVO";
    private static final String INACTIVO = "INACTIVO";

    private final ConsultaRepository consultaRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final ConfiguracionService configuracionService;
    private final AuthService authService;

    public ClienteAdminServiceImpl(ConsultaRepository consultaRepository,
                                   ProcedimientoRepository procedimientoRepository,
                                   ConfiguracionService configuracionService,
                                   AuthService authService) {
        this.consultaRepository = consultaRepository;
        this.procedimientoRepository = procedimientoRepository;
        this.configuracionService = configuracionService;
        this.authService = authService;
    }

    @Override
    public List<ClienteAdminRs> listar() {
        int diasInactividad = configuracionService.obtenerDiasInactividad();
        List<ClienteAdminRs> clientes =
                consultaRepository.listarClientesAdmin(configuracionService.obtenerDiasCalificado());
        clientes.forEach(cliente -> cliente.setInactivable(esInactivable(cliente, diasInactividad)));
        return clientes;
    }

    @Override
    public RespuestaRs crear(RegistroClienteRq registroRq) {
        RespuestaRs respuesta = authService.registrarCliente(registroRq);
        return RespuestaRs.ok("Cliente creado correctamente.", respuesta.getId());
    }

    @Override
    public RespuestaRs inactivar(Integer idCliente) {
        ClienteAdminRs cliente = obtener(idCliente);
        int diasInactividad = configuracionService.obtenerDiasInactividad();
        if (INACTIVO.equals(cliente.getEstado())) {
            throw new BadRequestException("El cliente ya está inactivo.");
        }
        if (Boolean.TRUE.equals(cliente.getTieneLicencia())) {
            throw new BadRequestException("El cliente tiene una licencia comprada y no se puede inactivar.");
        }
        if (cliente.getDiasSinIngresar() == null || cliente.getDiasSinIngresar() < diasInactividad) {
            throw new BadRequestException("El cliente ingresó hace " + cliente.getDiasSinIngresar()
                    + " días. Solo se puede inactivar después de " + diasInactividad + " días sin ingresar.");
        }
        procedimientoRepository.cambiarEstadoUsuario(cliente.getIdUsuario(), INACTIVO);
        return RespuestaRs.ok("Cliente inactivado. Su historial se conserva.", idCliente);
    }

    @Override
    public RespuestaRs activar(Integer idCliente) {
        ClienteAdminRs cliente = obtener(idCliente);
        if (ACTIVO.equals(cliente.getEstado())) {
            throw new BadRequestException("El cliente ya está activo.");
        }
        procedimientoRepository.cambiarEstadoUsuario(cliente.getIdUsuario(), ACTIVO);
        return RespuestaRs.ok("Cliente activado.", idCliente);
    }

    @Override
    public RespuestaRs inactivarInactivos() {
        List<ClienteAdminRs> inactivables = listar().stream()
                .filter(ClienteAdminRs::getInactivable)
                .toList();
        inactivables.forEach(c -> procedimientoRepository.cambiarEstadoUsuario(c.getIdUsuario(), INACTIVO));
        return RespuestaRs.ok(inactivables.isEmpty()
                ? "No hay clientes que cumplan la regla de inactividad."
                : "Se inactivaron " + inactivables.size() + " clientes sin ingresos ni licencia.",
                inactivables.size());
    }

    private ClienteAdminRs obtener(Integer idCliente) {
        return consultaRepository.obtenerClienteAdmin(idCliente, configuracionService.obtenerDiasCalificado())
                .orElseThrow(() -> new RecursoNoEncontradoException("El cliente no existe."));
    }

    private boolean esInactivable(ClienteAdminRs cliente, int diasInactividad) {
        return ACTIVO.equals(cliente.getEstado())
                && !Boolean.TRUE.equals(cliente.getTieneLicencia())
                && cliente.getDiasSinIngresar() != null
                && cliente.getDiasSinIngresar() >= diasInactividad;
    }
}
