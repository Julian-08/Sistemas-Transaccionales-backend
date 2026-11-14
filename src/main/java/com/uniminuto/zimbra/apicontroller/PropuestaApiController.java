package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.PropuestaApi;
import com.uniminuto.zimbra.models.CambiarEstadoPropuestaRq;
import com.uniminuto.zimbra.models.PropuestaRq;
import com.uniminuto.zimbra.models.PropuestaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.SesionActual;
import com.uniminuto.zimbra.service.PropuestaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PropuestaApiController implements PropuestaApi {

    private final PropuestaService propuestaService;

    public PropuestaApiController(PropuestaService propuestaService) {
        this.propuestaService = propuestaService;
    }

    @Override
    public ResponseEntity<RespuestaRs> crear(PropuestaRq propuestaRq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(propuestaService.crear(propuestaRq, SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<List<PropuestaRs>> listar(Integer idCliente, String estado) {
        return ResponseEntity.ok(propuestaService.listar(SesionActual.obtener(), idCliente, estado));
    }

    @Override
    public ResponseEntity<RespuestaRs> cambiarEstado(CambiarEstadoPropuestaRq estadoRq) {
        return ResponseEntity.ok(propuestaService.cambiarEstado(estadoRq, SesionActual.obtener()));
    }
}
