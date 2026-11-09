package br.pucminas.locadora.service;

import br.pucminas.locadora.dto.Dtos.*;
import br.pucminas.locadora.model.*;
import br.pucminas.locadora.repository.PedidoAluguelRepository;
import br.pucminas.locadora.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoAluguelRepository pedidos;
    private final ClienteService clienteService;
    private final AutomovelService automovelService;
    private final UsuarioRepository usuarios;

    public PedidoService(PedidoAluguelRepository pedidos, ClienteService clienteService,
                         AutomovelService automovelService, UsuarioRepository usuarios) {
        this.pedidos = pedidos;
        this.clienteService = clienteService;
        this.automovelService = automovelService;
        this.usuarios = usuarios;
    }

    @Transactional
    public PedidoAluguel criar(String login, PedidoDados d) {
        validarPeriodo(d);
        PedidoAluguel p = new PedidoAluguel();
        p.setCliente(clienteService.doLogin(login));
        p.setAutomovel(automovelService.buscar(d.automovelId()));
        p.setDataInicio(d.dataInicio());
        p.setDataFim(d.dataFim());
        p.setStatus(StatusPedido.EM_ANALISE);
        return pedidos.save(p);
    }

    @Transactional(readOnly = true)
    public List<PedidoAluguel> listarDoCliente(String login) {
        return pedidos.findByClienteId(clienteService.doLogin(login).getId());
    }

    @Transactional(readOnly = true)
    public List<PedidoAluguel> listarTodos() {
        return pedidos.findAll();
    }

    @Transactional(readOnly = true)
    public PedidoAluguel buscarDoCliente(String login, Long id) {
        PedidoAluguel p = buscar(id);
        if (!p.getCliente().getId().equals(clienteService.doLogin(login).getId())) {
            throw new NegocioException(HttpStatus.NOT_FOUND, "Pedido nao encontrado");
        }
        return p;
    }

    @Transactional
    public PedidoAluguel modificar(String login, Long id, PedidoDados d) {
        validarPeriodo(d);
        PedidoAluguel p = buscarDoCliente(login, id);
        if (p.getStatus() != StatusPedido.EM_ANALISE) {
            throw new NegocioException(HttpStatus.CONFLICT, "So e possivel modificar pedidos em analise");
        }
        p.setAutomovel(automovelService.buscar(d.automovelId()));
        p.setDataInicio(d.dataInicio());
        p.setDataFim(d.dataFim());
        return pedidos.save(p);
    }

    @Transactional
    public PedidoAluguel cancelar(String login, Long id) {
        PedidoAluguel p = buscarDoCliente(login, id);
        if (p.getStatus() == StatusPedido.CONTRATADO || p.getStatus() == StatusPedido.CANCELADO) {
            throw new NegocioException(HttpStatus.CONFLICT, "Pedido nao pode mais ser cancelado");
        }
        p.setStatus(StatusPedido.CANCELADO);
        return pedidos.save(p);
    }

    @Transactional
    public PedidoAluguel avaliar(String loginAgente, Long id, AvaliacaoPedido a) {
        PedidoAluguel p = buscar(id);
        Usuario agente = usuarios.findByLogin(loginAgente)
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, "Agente nao encontrado"));
        StatusPedido novo = a.status();
        boolean valida = switch (novo) {
            case APROVADO, REPROVADO -> p.getStatus() == StatusPedido.EM_ANALISE;
            case CONTRATADO -> p.getStatus() == StatusPedido.APROVADO;
            default -> false;
        };
        if (!valida) {
            throw new NegocioException(HttpStatus.CONFLICT,
                    "Transicao invalida: " + p.getStatus() + " -> " + novo);
        }
        p.setStatus(novo);
        p.setParecer(a.parecer());
        if (novo == StatusPedido.APROVADO && agente.getTipoAgente() == TipoAgente.BANCO) {
            p.setBancoCredito(agente);
        }
        return pedidos.save(p);
    }

    private PedidoAluguel buscar(Long id) {
        return pedidos.findById(id)
                .orElseThrow(() -> new NegocioException(HttpStatus.NOT_FOUND, "Pedido nao encontrado"));
    }

    private void validarPeriodo(PedidoDados d) {
        if (!d.dataFim().isAfter(d.dataInicio())) {
            throw new NegocioException(HttpStatus.BAD_REQUEST, "dataFim deve ser posterior a dataInicio");
        }
    }
}
