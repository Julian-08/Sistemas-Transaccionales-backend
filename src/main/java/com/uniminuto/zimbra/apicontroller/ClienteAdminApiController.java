package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.ClienteAdminApi;
import com.uniminuto.zimbra.models.ClienteAdminRs;
import com.uniminuto.zimbra.models.RegistroClienteRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.service.ClienteAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClienteAdminApiController implements ClienteAdminApi {

    private final ClienteAdminService clienteAdminService;

    public ClienteAdminApiController(ClienteAdminService clienteAdminService) {
        this.clienteAdminService = clienteAdminService;
    }

    @Override
    public ResponseEntity<List<ClienteAdminRs>> listar() {
        return ResponseEntity.ok(clienteAdminService.listar());
    }

    @Override
    public ResponseEntity<RespuestaRs> crear(RegistroClienteRq registroRq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteAdminService.crear(registroRq));
    }

    @Override
    public ResponseEntity<RespuestaRs> inactivar(Integer idCliente) {
        return ResponseEntity.ok(clienteAdminService.inactivar(idCliente));
    }

    @Override
    public ResponseEntity<RespuestaRs> activar(Integer idCliente) {
        return ResponseEntity.ok(clienteAdminService.activar(idCliente));
    }

    @Override
    public ResponseEntity<RespuestaRs> inactivarInactivos() {
        return ResponseEntity.ok(clienteAdminService.inactivarInactivos());
    }
}
