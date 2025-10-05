package org.projeto.javafxmaven.repository;

import org.projeto.javafxmaven.modelo.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findAllByOrderByDataPublicacaoDesc();

}
