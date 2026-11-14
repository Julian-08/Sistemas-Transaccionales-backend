package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.ClienteApi;
import com.uniminuto.zimbra.models.ActividadRq;
import com.uniminuto.zimbra.models.PerfilClienteRs;
import com.uniminuto.zimbra.models.PruebaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.SesionActual;
import com.uniminuto.zimbra.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClienteApiController implements ClienteApi {

    private final ClienteService clienteService;

    public ClienteApiController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public ResponseEntity<PerfilClienteRs> obtenerPerfil() {
        return ResponseEntity.ok(clienteService.obtenerPerfil(SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<RespuestaRs> descargarPrueba() {
        return ResponseEntity.ok(clienteService.descargarPrueba(SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<List<PruebaRs>> listarPruebas() {
        return ResponseEntity.ok(clienteService.listarPruebas(SesionActual.obtener()));
    }

    @Override
    public ResponseEntity<Void> registrarActividad(ActividadRq actividadRq) {
        clienteService.registrarActividad(actividadRq, SesionActual.obtener());
        return ResponseEntity.accepted().build();
    }
}
