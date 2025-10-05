package org.projeto.javafxmaven.repository;

import org.projeto.javafxmaven.modelo.Trabalhador;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrabalhadorRepository extends CrudRepository<Trabalhador, Long> {
    List<Trabalhador> findByNome(String nome);
    Optional<Trabalhador> findByEmail(String email);
    List<Trabalhador> findByNomeContaining(String nome);
    Optional<Trabalhador> findByCpf(String cpf);
    List<Trabalhador> findByNomeAndEmail(String nome, String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
