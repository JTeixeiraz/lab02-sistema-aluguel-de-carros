package br.pucminas.locadora.service;

import br.pucminas.locadora.dto.Dtos.*;
import br.pucminas.locadora.model.Cliente;
import br.pucminas.locadora.model.Empregador;
import br.pucminas.locadora.repository.ClienteRepository;
import br.pucminas.locadora.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;

    public ClienteService(ClienteRepository clientes, UsuarioRepository usuarios) {
        this.clientes = clientes;
        this.usuarios = usuarios;
    }

    public Cliente doLogin(String login) {
        var u = usuarios.findByLogin(login)
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, "Usuario nao encontrado"));
        return clientes.findByUsuarioId(u.getId())
                .orElseThrow(() -> new NegocioException(HttpStatus.NOT_FOUND, "Usuario nao e cliente"));
    }

    public Cliente buscar(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new NegocioException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
    }

    public List<Cliente> listar() {
        return clientes.findAll();
    }

    @Transactional
    public Cliente atualizar(Cliente c, ClienteDados d) {
        validarUnicidade(d, c);
        aplicar(c, d);
        return clientes.save(c);
    }

    @Transactional
    public void remover(Cliente c) {
        Long usuarioId = c.getUsuario().getId();
        clientes.delete(c);
        usuarios.deleteById(usuarioId);
    }

    void validarUnicidade(ClienteDados d, Cliente atual) {
        boolean cpfEmUso = clientes.existsByCpf(d.cpf()) && (atual == null || !atual.getCpf().equals(d.cpf()));
        boolean rgEmUso = clientes.existsByRg(d.rg()) && (atual == null || !atual.getRg().equals(d.rg()));
        if (cpfEmUso) throw new NegocioException(HttpStatus.CONFLICT, "CPF ja cadastrado");
        if (rgEmUso) throw new NegocioException(HttpStatus.CONFLICT, "RG ja cadastrado");
    }

    void aplicar(Cliente c, ClienteDados d) {
        c.setRg(d.rg());
        c.setCpf(d.cpf());
        c.setNome(d.nome());
        c.setEndereco(d.endereco());
        c.setProfissao(d.profissao());
        c.getEmpregadores().clear();
        if (d.empregadores() != null) {
            if (d.empregadores().size() > Cliente.MAX_EMPREGADORES) {
                throw new NegocioException(HttpStatus.BAD_REQUEST, "Maximo de 3 empregadores");
            }
            for (EmpregadorDto e : d.empregadores()) {
                Empregador emp = new Empregador();
                emp.setNome(e.nome());
                emp.setRendimento(e.rendimento());
                c.getEmpregadores().add(emp);
            }
        }
    }
}
