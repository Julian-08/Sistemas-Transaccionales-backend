package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.entity.PropuestaRfp;
import com.uniminuto.zimbra.exception.AccesoDenegadoException;
import com.uniminuto.zimbra.exception.BadRequestException;
import com.uniminuto.zimbra.exception.RecursoNoEncontradoException;
import com.uniminuto.zimbra.models.CambiarEstadoPropuestaRq;
import com.uniminuto.zimbra.models.PropuestaRq;
import com.uniminuto.zimbra.models.PropuestaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.repository.ConsultaRepository;
import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.repository.PropuestaRfpRepository;
import com.uniminuto.zimbra.security.UsuarioSesion;
import com.uniminuto.zimbra.security.ValidadorSesion;
import com.uniminuto.zimbra.service.PropuestaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class PropuestaServiceImpl implements PropuestaService {

    /** Estados finales: ya no se pueden cambiar a mano. */
    private static final Set<String> ESTADOS_FINALES = Set.of("ACEPTADA", "RECHAZADA");

    private final ProcedimientoRepository procedimientoRepository;
    private final PropuestaRfpRepository propuestaRepository;
    private final ConsultaRepository consultaRepository;

    public PropuestaServiceImpl(ProcedimientoRepository procedimientoRepository,
                                PropuestaRfpRepository propuestaRepository,
                                ConsultaRepository consultaRepository) {
        this.procedimientoRepository = procedimientoRepository;
        this.propuestaRepository = propuestaRepository;
        this.consultaRepository = consultaRepository;
    }

    @Override
    public RespuestaRs crear(PropuestaRq propuestaRq, UsuarioSesion sesion) {
        Integer idAsesor = ValidadorSesion.idAsesor(sesion);
        // sp_crear_propuesta valida que el prospecto sea del asesor y no este cerrado ni descartado.
        Integer idPropuesta = procedimientoRepository.crearPropuesta(
                propuestaRq.getIdCliente(), idAsesor, propuestaRq.getMontoEstimado());
        return RespuestaRs.ok("Propuesta creada correctamente.", idPropuesta);
    }

    @Override
    public List<PropuestaRs> listar(UsuarioSesion sesion, Integer idCliente, String estado) {
        Integer idAsesor = sesion.esAdministrador() ? null : ValidadorSesion.idAsesor(sesion);
        return consultaRepository.listarPropuestas(idAsesor, idCliente, estado);
    }

    @Override
    public RespuestaRs cambiarEstado(CambiarEstadoPropuestaRq estadoRq, UsuarioSesion sesion) {
        Integer idAsesor = ValidadorSesion.idAsesor(sesion);
        PropuestaRfp propuesta = propuestaRepository.findById(estadoRq.getIdPropuesta())
                .orElseThrow(() -> new RecursoNoEncontradoException("La propuesta no existe."));
        if (!Objects.equals(propuesta.getIdAsesor(), idAsesor)) {
            throw new AccesoDenegadoException("Solo puede modificar sus propias propuestas.");
        }
        if (ESTADOS_FINALES.contains(propuesta.getEstado())) {
            throw new BadRequestException("La propuesta ya está " + propuesta.getEstado() + " y no se puede cambiar.");
        }
        propuesta.setEstado(estadoRq.getEstado());
        propuestaRepository.save(propuesta);
        return RespuestaRs.ok("Propuesta actualizada a " + estadoRq.getEstado() + ".", propuesta.getIdPropuesta());
    }
}
