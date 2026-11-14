package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.models.AlertaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.repository.AlertaRepository;
import com.uniminuto.zimbra.repository.ConsultaRepository;
import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.security.UsuarioSesion;
import com.uniminuto.zimbra.service.AlertaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertaServiceImpl implements AlertaService {

    private final ConsultaRepository consultaRepository;
    private final AlertaRepository alertaRepository;
    private final ProcedimientoRepository procedimientoRepository;

    public AlertaServiceImpl(ConsultaRepository consultaRepository,
                             AlertaRepository alertaRepository,
                             ProcedimientoRepository procedimientoRepository) {
        this.consultaRepository = consultaRepository;
        this.alertaRepository = alertaRepository;
        this.procedimientoRepository = procedimientoRepository;
    }

    @Override
    public List<AlertaRs> listarMisAlertas(UsuarioSesion sesion, boolean soloNoLeidas) {
        return consultaRepository.listarAlertas(sesion.idUsuario(), soloNoLeidas);
    }

    @Override
    public long contarNoLeidas(UsuarioSesion sesion) {
        return alertaRepository.countByIdUsuarioDestinoAndLeidaFalse(sesion.idUsuario());
    }

    @Override
    public RespuestaRs marcarLeida(Integer idAlerta, UsuarioSesion sesion) {
        // sp_marcar_alerta_leida valida que la alerta sea del usuario.
        procedimientoRepository.marcarAlertaLeida(idAlerta, sesion.idUsuario());
        return RespuestaRs.ok("Alerta marcada como leída.", idAlerta);
    }
}
