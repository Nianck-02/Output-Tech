package org.projeto.javafxmaven.repository;

import org.projeto.javafxmaven.modelo.Divulgar;
import org.projeto.javafxmaven.modelo.Trabalhador;
import org.projeto.javafxmaven.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DivulgarRepository extends JpaRepository<Divulgar, Long> {
    List<Divulgar> findAllByOrderByDataPublicacaoDesc();
    List<Divulgar> findByUsuarioOrderByDataPublicacaoDesc(Usuario usuario);
    List<Divulgar> findByTrabalhadorOrderByDataPublicacaoDesc(Trabalhador trabalhador);
}
