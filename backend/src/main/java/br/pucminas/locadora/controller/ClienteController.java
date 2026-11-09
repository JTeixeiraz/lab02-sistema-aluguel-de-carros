package br.pucminas.locadora.controller;

import br.pucminas.locadora.dto.Dtos.*;
import br.pucminas.locadora.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ClienteResponse me(Authentication a) {
        return ClienteResponse.de(service.doLogin(a.getName()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ClienteResponse atualizar(Authentication a, @Valid @RequestBody ClienteDados d) {
        return ClienteResponse.de(service.atualizar(service.doLogin(a.getName()), d));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(Authentication a) {
        service.remover(service.doLogin(a.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('AGENTE')")
    public List<ClienteResponse> listar() {
        return service.listar().stream().map(ClienteResponse::de).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('AGENTE')")
    public ClienteResponse buscar(@PathVariable Long id) {
        return ClienteResponse.de(service.buscar(id));
    }
}
