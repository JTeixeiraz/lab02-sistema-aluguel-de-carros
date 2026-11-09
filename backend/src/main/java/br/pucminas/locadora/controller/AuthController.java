package br.pucminas.locadora.controller;

import br.pucminas.locadora.dto.Dtos.*;
import br.pucminas.locadora.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest r) {
        return auth.login(r);
    }

    @PostMapping("/registro/cliente")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse registrarCliente(@Valid @RequestBody RegistroCliente r) {
        return auth.registrarCliente(r);
    }

    @PostMapping("/registro/agente")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse registrarAgente(@Valid @RequestBody RegistroAgente r) {
        return auth.registrarAgente(r);
    }
}
