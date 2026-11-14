package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.AsesorApi;
import com.uniminuto.zimbra.models.AsesorRs;
import com.uniminuto.zimbra.models.EstadisticasAsesorRs;
import com.uniminuto.zimbra.models.MetaAsesorRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.SesionActual;
import com.uniminuto.zimbra.service.AsesorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AsesorApiController implements AsesorApi {

    private final AsesorService asesorService;

    public AsesorApiController(AsesorService asesorService) {
        this.asesorService = asesorService;
    }

    @Override
    public ResponseEntity<List<AsesorRs>> listar(boolean soloActivos) {
        return ResponseEntity.ok(asesorService.listar(soloActivos));
    }

    @Override
    public ResponseEntity<EstadisticasAsesorRs> misEstadisticas() {
        return ResponseEntity.ok(asesorService.misEstadisticas(SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<RespuestaRs> actualizarMeta(MetaAsesorRq metaRq) {
        return ResponseEntity.ok(asesorService.actualizarMeta(metaRq));
    }
}
