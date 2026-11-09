package br.pucminas.locadora.controller;

import br.pucminas.locadora.dto.Dtos.*;
import br.pucminas.locadora.service.AutomovelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/automoveis")
public class AutomovelController {

    private final AutomovelService service;

    public AutomovelController(AutomovelService service) {
        this.service = service;
    }

    @GetMapping
    public List<AutomovelResponse> listar() {
        return service.listar().stream().map(AutomovelResponse::de).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('AGENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public AutomovelResponse criar(@Valid @RequestBody AutomovelDados d) {
        return AutomovelResponse.de(service.criar(d));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AGENTE')")
    public AutomovelResponse atualizar(@PathVariable Long id, @Valid @RequestBody AutomovelDados d) {
        return AutomovelResponse.de(service.atualizar(id, d));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGENTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}
