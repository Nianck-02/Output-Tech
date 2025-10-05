package org.projeto.javafxmaven.repository;

import org.projeto.javafxmaven.modelo.Pessoa;
import org.projeto.javafxmaven.modelo.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends CrudRepository<Usuario, Long> {
    Optional<Pessoa> findByEmail(String email);
    boolean existsByEmail(String email);
}
