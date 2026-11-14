package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.entity.AsesorComercial;
import com.uniminuto.zimbra.exception.RecursoNoEncontradoException;
import com.uniminuto.zimbra.models.AsesorRs;
import com.uniminuto.zimbra.models.EstadisticasAsesorRs;
import com.uniminuto.zimbra.models.MetaAsesorRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.repository.AlertaRepository;
import com.uniminuto.zimbra.repository.AsesorComercialRepository;
import com.uniminuto.zimbra.repository.ConsultaRepository;
import com.uniminuto.zimbra.security.UsuarioSesion;
import com.uniminuto.zimbra.security.ValidadorSesion;
import com.uniminuto.zimbra.service.AsesorService;
import com.uniminuto.zimbra.service.ConfiguracionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsesorServiceImpl implements AsesorService {

    private static final List<String> ESTADOS =
            List.of("NUEVO", "CONTACTADO", "INTERESADO", "CERRADO", "DESCARTADO");

    private final AsesorComercialRepository asesorRepository;
    private final ConsultaRepository consultaRepository;
    private final AlertaRepository alertaRepository;
    private final ConfiguracionService configuracionService;

    public AsesorServiceImpl(AsesorComercialRepository asesorRepository,
                             ConsultaRepository consultaRepository,
                             AlertaRepository alertaRepository,
                             ConfiguracionService configuracionService) {
        this.asesorRepository = asesorRepository;
        this.consultaRepository = consultaRepository;
        this.alertaRepository = alertaRepository;
        this.configuracionService = configuracionService;
    }

    @Override
    public List<AsesorRs> listar(boolean soloActivos) {
        return consultaRepository.listarAsesores(soloActivos);
    }

    @Override
    public RespuestaRs actualizarMeta(MetaAsesorRq metaRq) {
        AsesorComercial asesor = asesorRepository.findById(metaRq.getIdAsesor())
                .orElseThrow(() -> new RecursoNoEncontradoException("El asesor no existe."));
        asesor.setMetaMensual(metaRq.getMetaMensual());
        asesorRepository.save(asesor);
        return RespuestaRs.ok("Meta mensual actualizada.", asesor.getIdAsesor());
    }

    @Override
    public EstadisticasAsesorRs misEstadisticas(UsuarioSesion sesion) {
        Integer idAsesor = ValidadorSesion.idAsesor(sesion);
        AsesorComercial asesor = asesorRepository.findById(idAsesor)
                .orElseThrow(() -> new RecursoNoEncontradoException("El asesor no existe."));

        Map<String, Integer> porEstado = new LinkedHashMap<>();
        ESTADOS.forEach(estado -> porEstado.put(estado, 0));
        consultaRepository.prospectosPorEstado(idAsesor).forEach(fila ->
                porEstado.put((String) fila.get("estado"), ((Number) fila.get("cantidad")).intValue()));

        Map<String, Object> resumen = consultaRepository.resumenAsesor(idAsesor, LocalDate.now().withDayOfMonth(1));
        BigDecimal montoMes = new BigDecimal(resumen.get("monto_mes").toString());
        BigDecimal meta = asesor.getMetaMensual() == null ? BigDecimal.ZERO : asesor.getMetaMensual();

        EstadisticasAsesorRs rs = new EstadisticasAsesorRs();
        rs.setProspectosPorEstado(porEstado);
        rs.setProspectosAsignados(porEstado.values().stream().mapToInt(Integer::intValue).sum());
        rs.setClientesCalificados(consultaRepository.listarCalificados(idAsesor, false,
                configuracionService.obtenerIngresosCalificado(), configuracionService.obtenerDiasCalificado()).size());
        rs.setAlertasNoLeidas(alertaRepository.countByIdUsuarioDestinoAndLeidaFalse(sesion.idUsuario()));
        rs.setSeguimientosMes(((Number) resumen.get("seguimientos_mes")).intValue());
        rs.setPropuestasAbiertas(((Number) resumen.get("propuestas_abiertas")).intValue());
        rs.setVentasMes(((Number) resumen.get("ventas_mes")).intValue());
        rs.setMontoVendidoMes(montoMes);
        rs.setMetaMensual(meta);
        rs.setCumplimientoMetaPct(meta.signum() == 0 ? BigDecimal.ZERO
                : montoMes.multiply(BigDecimal.valueOf(100)).divide(meta, 2, RoundingMode.HALF_UP));
        return rs;
    }
}
