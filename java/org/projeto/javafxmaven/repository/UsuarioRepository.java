package org.projeto.javafxmaven.repository;

import org.projeto.javafxmaven.modelo.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    List<Usuario> findByNome(String nome);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByNomeContaining(String nome);
    Optional<Usuario> findByCpf(String cpf);
    List<Usuario> findByNomeAndEmail(String nome, String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
