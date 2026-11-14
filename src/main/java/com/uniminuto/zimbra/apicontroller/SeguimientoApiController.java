package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.SeguimientoApi;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.SeguimientoRq;
import com.uniminuto.zimbra.models.SeguimientoRs;
import com.uniminuto.zimbra.security.SesionActual;
import com.uniminuto.zimbra.service.SeguimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SeguimientoApiController implements SeguimientoApi {

    private final SeguimientoService seguimientoService;

    public SeguimientoApiController(SeguimientoService seguimientoService) {
        this.seguimientoService = seguimientoService;
    }

    @Override
    public ResponseEntity<RespuestaRs> registrar(SeguimientoRq seguimientoRq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seguimientoService.registrar(seguimientoRq, SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<List<SeguimientoRs>> historialProspecto(Integer idCliente) {
        return ResponseEntity.ok(seguimientoService.historialProspecto(idCliente, SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<List<SeguimientoRs>> listar(Integer idAsesor, LocalDate desde, LocalDate hasta) {
        return ResponseEntity.ok(seguimientoService.listar(idAsesor, desde, hasta));
    }
}
