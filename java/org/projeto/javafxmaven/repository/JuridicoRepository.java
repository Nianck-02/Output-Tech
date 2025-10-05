package org.projeto.javafxmaven.repository;

import org.projeto.javafxmaven.modelo.Juridico;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JuridicoRepository extends CrudRepository<Juridico, Long> {
    List<Juridico> findByNome(String nome);
    Optional<Juridico> findByEmail(String email);
    List<Juridico> findByNomeContaining(String nome);
    List<Juridico> findByNomeAndEmail(String nome, String email);
    boolean existsByEmail(String email);
    boolean existsByCnpj(String cnpj);
    Optional<Object> findByCnpj(String cnpj);
}
