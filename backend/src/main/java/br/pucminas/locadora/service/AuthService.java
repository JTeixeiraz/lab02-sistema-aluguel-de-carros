package br.pucminas.locadora.service;

import br.pucminas.locadora.dto.Dtos.*;
import br.pucminas.locadora.model.*;
import br.pucminas.locadora.repository.ClienteRepository;
import br.pucminas.locadora.repository.UsuarioRepository;
import br.pucminas.locadora.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final ClienteRepository clientes;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final ClienteService clienteService;
    private final String codigoConvite;

    public AuthService(UsuarioRepository usuarios, ClienteRepository clientes, PasswordEncoder encoder,
                       JwtService jwt, ClienteService clienteService,
                       @Value("${app.agente-codigo}") String codigoConvite) {
        this.usuarios = usuarios;
        this.clientes = clientes;
        this.encoder = encoder;
        this.jwt = jwt;
        this.clienteService = clienteService;
        this.codigoConvite = codigoConvite;
    }

    @Transactional
    public LoginResponse registrarCliente(RegistroCliente r) {
        if (usuarios.existsByLogin(r.login())) {
            throw new NegocioException(HttpStatus.CONFLICT, "Login ja cadastrado");
        }
        clienteService.validarUnicidade(r.dados(), null);
        Usuario u = novoUsuario(r.login(), r.senha(), Perfil.CLIENTE, null);
        Cliente c = new Cliente();
        c.setUsuario(u);
        clienteService.aplicar(c, r.dados());
        clientes.save(c);
        return resposta(u);
    }

    @Transactional
    public LoginResponse registrarAgente(RegistroAgente r) {
        if (!codigoConvite.equals(r.codigoConvite())) {
            throw new NegocioException(HttpStatus.FORBIDDEN, "Codigo de convite invalido");
        }
        if (usuarios.existsByLogin(r.login())) {
            throw new NegocioException(HttpStatus.CONFLICT, "Login ja cadastrado");
        }
        return resposta(novoUsuario(r.login(), r.senha(), Perfil.AGENTE, r.tipoAgente()));
    }

    public LoginResponse login(LoginRequest r) {
        Usuario u = usuarios.findByLogin(r.login())
                .filter(x -> encoder.matches(r.senha(), x.getSenhaHash()))
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, "Usuario ou senha invalidos"));
        return resposta(u);
    }

    private Usuario novoUsuario(String login, String senha, Perfil perfil, TipoAgente tipo) {
        Usuario u = new Usuario();
        u.setLogin(login);
        u.setSenhaHash(encoder.encode(senha));
        u.setPerfil(perfil);
        u.setTipoAgente(tipo);
        return usuarios.save(u);
    }

    private LoginResponse resposta(Usuario u) {
        return new LoginResponse(jwt.gerar(u), u.getLogin(), u.getPerfil(), u.getTipoAgente());
    }
}
