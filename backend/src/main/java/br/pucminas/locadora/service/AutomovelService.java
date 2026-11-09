package br.pucminas.locadora.service;

import br.pucminas.locadora.dto.Dtos.AutomovelDados;
import br.pucminas.locadora.model.Automovel;
import br.pucminas.locadora.repository.AutomovelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutomovelService {

    private final AutomovelRepository repo;

    public AutomovelService(AutomovelRepository repo) {
        this.repo = repo;
    }

    public List<Automovel> listar() {
        return repo.findAll();
    }

    public Automovel buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NegocioException(HttpStatus.NOT_FOUND, "Automovel nao encontrado"));
    }

    public Automovel criar(AutomovelDados d) {
        if (repo.existsByPlaca(d.placa())) throw new NegocioException(HttpStatus.CONFLICT, "Placa ja cadastrada");
        if (repo.existsByMatricula(d.matricula())) throw new NegocioException(HttpStatus.CONFLICT, "Matricula ja cadastrada");
        return repo.save(aplicar(new Automovel(), d));
    }

    public Automovel atualizar(Long id, AutomovelDados d) {
        Automovel a = buscar(id);
        if (!a.getPlaca().equals(d.placa()) && repo.existsByPlaca(d.placa()))
            throw new NegocioException(HttpStatus.CONFLICT, "Placa ja cadastrada");
        if (!a.getMatricula().equals(d.matricula()) && repo.existsByMatricula(d.matricula()))
            throw new NegocioException(HttpStatus.CONFLICT, "Matricula ja cadastrada");
        return repo.save(aplicar(a, d));
    }

    public void remover(Long id) {
        repo.delete(buscar(id));
    }

    private Automovel aplicar(Automovel a, AutomovelDados d) {
        a.setMatricula(d.matricula());
        a.setAno(d.ano());
        a.setMarca(d.marca());
        a.setModelo(d.modelo());
        a.setPlaca(d.placa());
        a.setTipoProprietario(d.tipoProprietario());
        return a;
    }
}
