package br.pucminas.locadora.repository;

import br.pucminas.locadora.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuarioId(Long usuarioId);
    boolean existsByCpf(String cpf);
    boolean existsByRg(String rg);
}
