package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.ProspectoApi;
import com.uniminuto.zimbra.models.ActividadProspectoRs;
import com.uniminuto.zimbra.models.AsignarProspectoRq;
import com.uniminuto.zimbra.models.CambiarEstadoProspectoRq;
import com.uniminuto.zimbra.models.ClienteCalificadoRs;
import com.uniminuto.zimbra.models.ProspectoRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.SesionActual;
import com.uniminuto.zimbra.service.ProspectoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProspectoApiController implements ProspectoApi {

    private final ProspectoService prospectoService;

    public ProspectoApiController(ProspectoService prospectoService) {
        this.prospectoService = prospectoService;
    }

    @Override
    public ResponseEntity<List<ProspectoRs>> listarMisProspectos(String estado) {
        return ResponseEntity.ok(prospectoService.listarMisProspectos(SesionActual.obtener(), estado));
    }

    @Override
    public ResponseEntity<List<ProspectoRs>> listarTodos(Integer idAsesor, boolean sinAsignar, String estado) {
        return ResponseEntity.ok(prospectoService.listarTodos(idAsesor, sinAsignar, estado));
    }

    @Override
    public ResponseEntity<ActividadProspectoRs> consultarActividad(Integer idCliente) {
        return ResponseEntity.ok(prospectoService.consultarActividad(idCliente, SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<RespuestaRs> cambiarEstado(CambiarEstadoProspectoRq estadoRq) {
        return ResponseEntity.ok(prospectoService.cambiarEstado(estadoRq, SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<List<ClienteCalificadoRs>> listarCalificados(boolean sinAsignar) {
        return ResponseEntity.ok(prospectoService.listarCalificados(SesionActual.obtener(), sinAsignar));
    }

    @Override
    public ResponseEntity<RespuestaRs> asignar(AsignarProspectoRq asignarRq) {
        return ResponseEntity.ok(prospectoService.asignar(asignarRq));
    }
}
