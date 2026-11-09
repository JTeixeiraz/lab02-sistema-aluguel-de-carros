package br.pucminas.locadora.controller;

import br.pucminas.locadora.dto.Dtos.*;
import br.pucminas.locadora.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(Authentication a, @Valid @RequestBody PedidoDados d) {
        return PedidoResponse.de(service.criar(a.getName(), d));
    }

    @GetMapping
    public List<PedidoResponse> listar(Authentication a) {
        boolean agente = a.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_AGENTE"));
        var lista = agente ? service.listarTodos() : service.listarDoCliente(a.getName());
        return lista.stream().map(PedidoResponse::de).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse buscar(Authentication a, @PathVariable Long id) {
        return PedidoResponse.de(service.buscarDoCliente(a.getName(), id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse modificar(Authentication a, @PathVariable Long id, @Valid @RequestBody PedidoDados d) {
        return PedidoResponse.de(service.modificar(a.getName(), id, d));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse cancelar(Authentication a, @PathVariable Long id) {
        return PedidoResponse.de(service.cancelar(a.getName(), id));
    }

    @PatchMapping("/{id}/avaliacao")
    @PreAuthorize("hasRole('AGENTE')")
    public PedidoResponse avaliar(Authentication a, @PathVariable Long id, @Valid @RequestBody AvaliacaoPedido av) {
        return PedidoResponse.de(service.avaliar(a.getName(), id, av));
    }
}
