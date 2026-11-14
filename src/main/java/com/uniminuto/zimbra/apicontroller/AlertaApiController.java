package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.AlertaApi;
import com.uniminuto.zimbra.models.AlertaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.SesionActual;
import com.uniminuto.zimbra.service.AlertaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AlertaApiController implements AlertaApi {

    private final AlertaService alertaService;

    public AlertaApiController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @Override
    public ResponseEntity<List<AlertaRs>> listarMisAlertas(boolean soloNoLeidas) {
        return ResponseEntity.ok(alertaService.listarMisAlertas(SesionActual.obtener(), soloNoLeidas));
    }

    @Override
    public ResponseEntity<Map<String, Long>> contarNoLeidas() {
        return ResponseEntity.ok(Map.of("noLeidas", alertaService.contarNoLeidas(SesionActual.obtener())));
    }

    @Override
    public ResponseEntity<RespuestaRs> marcarLeida(Integer idAlerta) {
        return ResponseEntity.ok(alertaService.marcarLeida(idAlerta, SesionActual.obtener()));
    }
}
