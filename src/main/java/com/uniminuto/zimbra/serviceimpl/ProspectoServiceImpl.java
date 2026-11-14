package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.entity.ActividadMarketing;
import com.uniminuto.zimbra.entity.Ingreso;
import com.uniminuto.zimbra.exception.BadRequestException;
import com.uniminuto.zimbra.exception.RecursoNoEncontradoException;
import com.uniminuto.zimbra.models.ActividadProspectoRs;
import com.uniminuto.zimbra.models.AsignarProspectoRq;
import com.uniminuto.zimbra.models.CambiarEstadoProspectoRq;
import com.uniminuto.zimbra.models.ClienteCalificadoRs;
import com.uniminuto.zimbra.service.ConfiguracionService;
import com.uniminuto.zimbra.models.ProspectoRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.repository.ActividadMarketingRepository;
import com.uniminuto.zimbra.repository.ConsultaRepository;
import com.uniminuto.zimbra.repository.IngresoRepository;
import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.security.UsuarioSesion;
import com.uniminuto.zimbra.security.ValidadorSesion;
import com.uniminuto.zimbra.service.ProspectoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProspectoServiceImpl implements ProspectoService {

    private static final String ESTADO_CERRADO = "CERRADO";

    private final ConsultaRepository consultaRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final IngresoRepository ingresoRepository;
    private final ActividadMarketingRepository actividadRepository;
    private final ConfiguracionService configuracionService;

    public ProspectoServiceImpl(ConsultaRepository consultaRepository,
                                ProcedimientoRepository procedimientoRepository,
                                IngresoRepository ingresoRepository,
                                ActividadMarketingRepository actividadRepository,
                                ConfiguracionService configuracionService) {
        this.consultaRepository = consultaRepository;
        this.procedimientoRepository = procedimientoRepository;
        this.ingresoRepository = ingresoRepository;
        this.actividadRepository = actividadRepository;
        this.configuracionService = configuracionService;
    }

    @Override
    public List<ProspectoRs> listarMisProspectos(UsuarioSesion sesion, String estado) {
        // RF-009: el asesor solo ve sus propios prospectos.
        return consultaRepository.listarProspectos(ValidadorSesion.idAsesor(sesion), false, estado);
    }

    @Override
    public List<ProspectoRs> listarTodos(Integer idAsesor, boolean sinAsignar, String estado) {
        return consultaRepository.listarProspectos(idAsesor, sinAsignar, estado);
    }

    @Override
    public ActividadProspectoRs consultarActividad(Integer idCliente, UsuarioSesion sesion) {
        ProspectoRs prospecto = obtenerProspecto(idCliente);
        ValidadorSesion.validarAccesoProspecto(sesion, prospecto.getIdAsesor());

        List<Ingreso> ingresos = ingresoRepository.findTop50ByIdUsuarioOrderByFechaHoraDesc(prospecto.getIdUsuario());
        List<ActividadMarketing> actividades = actividadRepository.findTop100ByIdClienteOrderByFechaHoraDesc(idCliente);

        ActividadProspectoRs rs = new ActividadProspectoRs();
        rs.setProspecto(prospecto);
        rs.setTotalIngresos(ingresoRepository.countByIdUsuario(prospecto.getIdUsuario()));
        rs.setIngresos(ingresos);
        rs.setActividades(actividades);
        rs.setSeguimientos(consultaRepository.listarSeguimientos(idCliente, null, null, null));
        if (ingresos.isEmpty() && actividades.isEmpty()) {
            rs.setMensaje("El prospecto no tiene actividad registrada.");
        }
        return rs;
    }

    @Override
    public RespuestaRs cambiarEstado(CambiarEstadoProspectoRq estadoRq, UsuarioSesion sesion) {
        Integer idAsesor = ValidadorSesion.idAsesor(sesion);
        ProspectoRs prospecto = obtenerProspecto(estadoRq.getIdCliente());
        ValidadorSesion.validarAccesoProspecto(sesion, prospecto.getIdAsesor());

        // RF-013 A1: un prospecto cerrado solo se modifica con confirmacion.
        if (ESTADO_CERRADO.equals(prospecto.getEstadoProspecto()) && !Boolean.TRUE.equals(estadoRq.getConfirmar())) {
            throw new BadRequestException(
                    "El prospecto ya está CERRADO. Para modificarlo envíe \"confirmar\": true.");
        }
        procedimientoRepository.cambiarEstadoProspecto(estadoRq.getIdCliente(), idAsesor, estadoRq.getEstado());
        return RespuestaRs.ok("Estado del prospecto actualizado a " + estadoRq.getEstado() + ".",
                estadoRq.getIdCliente());
    }

    @Override
    public RespuestaRs asignar(AsignarProspectoRq asignarRq) {
        // sp_asignar_prospecto valida que sea prospecto y que el asesor este activo (RF-016 A1).
        procedimientoRepository.asignarProspecto(asignarRq.getIdCliente(), asignarRq.getIdAsesor());
        return RespuestaRs.ok("Prospecto asignado correctamente.", asignarRq.getIdCliente());
    }

    @Override
    public List<ClienteCalificadoRs> listarCalificados(UsuarioSesion sesion, boolean sinAsignar) {
        Integer idAsesor = sesion.esAdministrador() ? null : ValidadorSesion.idAsesor(sesion);
        return consultaRepository.listarCalificados(idAsesor, sesion.esAdministrador() && sinAsignar,
                configuracionService.obtenerIngresosCalificado(), configuracionService.obtenerDiasCalificado());
    }

    private ProspectoRs obtenerProspecto(Integer idCliente) {
        return consultaRepository.obtenerProspecto(idCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("El prospecto no existe."));
    }
}
