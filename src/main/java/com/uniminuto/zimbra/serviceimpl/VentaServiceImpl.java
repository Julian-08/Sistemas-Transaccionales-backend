package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.entity.PropuestaRfp;
import com.uniminuto.zimbra.exception.AccesoDenegadoException;
import com.uniminuto.zimbra.exception.BadRequestException;
import com.uniminuto.zimbra.exception.RecursoNoEncontradoException;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.VentaRq;
import com.uniminuto.zimbra.models.VentaRs;
import com.uniminuto.zimbra.repository.ConsultaRepository;
import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.repository.PropuestaRfpRepository;
import com.uniminuto.zimbra.repository.VentaLicenciaRepository;
import com.uniminuto.zimbra.security.UsuarioSesion;
import com.uniminuto.zimbra.security.ValidadorSesion;
import com.uniminuto.zimbra.service.VentaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class VentaServiceImpl implements VentaService {

    private final ProcedimientoRepository procedimientoRepository;
    private final PropuestaRfpRepository propuestaRepository;
    private final VentaLicenciaRepository ventaRepository;
    private final ConsultaRepository consultaRepository;

    public VentaServiceImpl(ProcedimientoRepository procedimientoRepository,
                            PropuestaRfpRepository propuestaRepository,
                            VentaLicenciaRepository ventaRepository,
                            ConsultaRepository consultaRepository) {
        this.procedimientoRepository = procedimientoRepository;
        this.propuestaRepository = propuestaRepository;
        this.ventaRepository = ventaRepository;
        this.consultaRepository = consultaRepository;
    }

    @Override
    public RespuestaRs registrar(VentaRq ventaRq, UsuarioSesion sesion) {
        Integer idAsesor = ValidadorSesion.idAsesor(sesion);
        PropuestaRfp propuesta = propuestaRepository.findById(ventaRq.getIdPropuesta())
                .orElseThrow(() -> new RecursoNoEncontradoException("La propuesta no existe."));
        if (!Objects.equals(propuesta.getIdAsesor(), idAsesor)) {
            throw new AccesoDenegadoException("Solo puede cerrar ventas de sus propias propuestas.");
        }
        if (ventaRepository.existsByIdPropuesta(ventaRq.getIdPropuesta())) {
            throw new BadRequestException("La propuesta ya tiene una venta registrada.");
        }
        // sp_registrar_venta: crea la venta, acepta la propuesta y cierra el prospecto en una transaccion.
        procedimientoRepository.registrarVenta(ventaRq.getIdPropuesta(), ventaRq.getMontoFinal(),
                ventaRq.getTipoLicencia(), ventaRq.getPeriodo());
        return RespuestaRs.ok("Venta registrada. La propuesta quedó ACEPTADA y el prospecto CERRADO.",
                ventaRq.getIdPropuesta());
    }

    @Override
    public List<VentaRs> listar(UsuarioSesion sesion, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BadRequestException("La fecha 'desde' no puede ser mayor que la fecha 'hasta'.");
        }
        Integer idAsesor = sesion.esAdministrador() ? null : ValidadorSesion.idAsesor(sesion);
        return consultaRepository.listarVentas(idAsesor, desde, hasta);
    }
}
