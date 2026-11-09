package br.pucminas.locadora.dto;

import br.pucminas.locadora.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class Dtos {
    private Dtos() {}

    public record EmpregadorDto(@NotBlank String nome, @NotNull @PositiveOrZero BigDecimal rendimento) {}

    public record ClienteDados(
            @NotBlank String rg,
            @NotBlank String cpf,
            @NotBlank String nome,
            @NotBlank String endereco,
            String profissao,
            @Size(max = 3, message = "maximo de 3 empregadores") List<@Valid EmpregadorDto> empregadores) {}

    public record RegistroCliente(
            @NotBlank @Size(min = 3, max = 60) String login,
            @NotBlank @Size(min = 6, max = 100) String senha,
            @NotNull @Valid ClienteDados dados) {}

    public record RegistroAgente(
            @NotBlank @Size(min = 3, max = 60) String login,
            @NotBlank @Size(min = 6, max = 100) String senha,
            @NotNull TipoAgente tipoAgente,
            @NotBlank String codigoConvite) {}

    public record LoginRequest(@NotBlank String login, @NotBlank String senha) {}

    public record LoginResponse(String token, String login, Perfil perfil, TipoAgente tipoAgente) {}

    public record ClienteResponse(Long id, String rg, String cpf, String nome, String endereco,
                                  String profissao, List<EmpregadorDto> empregadores) {
        public static ClienteResponse de(Cliente c) {
            return new ClienteResponse(c.getId(), c.getRg(), c.getCpf(), c.getNome(), c.getEndereco(),
                    c.getProfissao(),
                    c.getEmpregadores().stream().map(e -> new EmpregadorDto(e.getNome(), e.getRendimento())).toList());
        }
    }

    public record AutomovelDados(
            @NotBlank String matricula,
            @NotNull @Min(1900) @Max(2100) Integer ano,
            @NotBlank String marca,
            @NotBlank String modelo,
            @NotBlank String placa,
            @NotNull TipoProprietario tipoProprietario) {}

    public record AutomovelResponse(Long id, String matricula, Integer ano, String marca, String modelo,
                                    String placa, TipoProprietario tipoProprietario) {
        public static AutomovelResponse de(Automovel a) {
            return new AutomovelResponse(a.getId(), a.getMatricula(), a.getAno(), a.getMarca(), a.getModelo(),
                    a.getPlaca(), a.getTipoProprietario());
        }
    }

    public record PedidoDados(@NotNull Long automovelId, @NotNull @FutureOrPresent LocalDate dataInicio,
                              @NotNull LocalDate dataFim) {}

    public record AvaliacaoPedido(@NotNull StatusPedido status, String parecer) {}

    public record PedidoResponse(Long id, ClienteResponse cliente, AutomovelResponse automovel,
                                 LocalDate dataInicio, LocalDate dataFim, StatusPedido status,
                                 String parecer, String bancoCredito) {
        public static PedidoResponse de(PedidoAluguel p) {
            return new PedidoResponse(p.getId(), ClienteResponse.de(p.getCliente()),
                    AutomovelResponse.de(p.getAutomovel()), p.getDataInicio(), p.getDataFim(), p.getStatus(),
                    p.getParecer(), p.getBancoCredito() == null ? null : p.getBancoCredito().getLogin());
        }
    }
}
