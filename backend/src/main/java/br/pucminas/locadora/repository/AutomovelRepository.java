package br.pucminas.locadora.repository;

import br.pucminas.locadora.model.Automovel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {
    boolean existsByPlaca(String placa);
    boolean existsByMatricula(String matricula);
}
