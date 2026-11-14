package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.exception.BadRequestException;
import com.uniminuto.zimbra.exception.RecursoNoEncontradoException;
import com.uniminuto.zimbra.models.ProspectoRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.SeguimientoRq;
import com.uniminuto.zimbra.models.SeguimientoRs;
import com.uniminuto.zimbra.repository.ConsultaRepository;
import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.security.UsuarioSesion;
import com.uniminuto.zimbra.security.ValidadorSesion;
import com.uniminuto.zimbra.service.SeguimientoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SeguimientoServiceImpl implements SeguimientoService {

    private final ConsultaRepository consultaRepository;
    private final ProcedimientoRepository procedimientoRepository;

    public SeguimientoServiceImpl(ConsultaRepository consultaRepository,
                                  ProcedimientoRepository procedimientoRepository) {
        this.consultaRepository = consultaRepository;
        this.procedimientoRepository = procedimientoRepository;
    }

    @Override
    public RespuestaRs registrar(SeguimientoRq seguimientoRq, UsuarioSesion sesion) {
        Integer idAsesor = ValidadorSesion.idAsesor(sesion);
        // sp_registrar_seguimiento valida que el prospecto sea del asesor;
        // el trigger pasa el estado de NUEVO a CONTACTADO.
        procedimientoRepository.registrarSeguimiento(seguimientoRq.getIdCliente(), idAsesor,
                seguimientoRq.getMedio(), seguimientoRq.getObservaciones().trim());
        return RespuestaRs.ok("Seguimiento registrado correctamente.", seguimientoRq.getIdCliente());
    }

    @Override
    public List<SeguimientoRs> historialProspecto(Integer idCliente, UsuarioSesion sesion) {
        ProspectoRs prospecto = consultaRepository.obtenerProspecto(idCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("El prospecto no existe."));
        ValidadorSesion.validarAccesoProspecto(sesion, prospecto.getIdAsesor());
        return consultaRepository.listarSeguimientos(idCliente, null, null, null);
    }

    @Override
    public List<SeguimientoRs> listar(Integer idAsesor, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BadRequestException("La fecha 'desde' no puede ser mayor que la fecha 'hasta'.");
        }
        return consultaRepository.listarSeguimientos(null, idAsesor, desde, hasta);
    }
}
