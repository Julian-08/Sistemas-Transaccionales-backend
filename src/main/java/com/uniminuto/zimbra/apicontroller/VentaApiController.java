package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.VentaApi;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.VentaRq;
import com.uniminuto.zimbra.models.VentaRs;
import com.uniminuto.zimbra.security.SesionActual;
import com.uniminuto.zimbra.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class VentaApiController implements VentaApi {

    private final VentaService ventaService;

    public VentaApiController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @Override
    public ResponseEntity<RespuestaRs> registrar(VentaRq ventaRq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.registrar(ventaRq, SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<List<VentaRs>> listar(LocalDate desde, LocalDate hasta) {
        return ResponseEntity.ok(ventaService.listar(SesionActual.obtener(), desde, hasta));
    }
}
