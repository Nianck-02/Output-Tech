package org.projeto.javafxmaven.Config;

import jakarta.annotation.PostConstruct;
import org.projeto.javafxmaven.modelo.Divulgar;
import org.projeto.javafxmaven.modelo.Pessoa;
import org.projeto.javafxmaven.modelo.Trabalhador;
import org.projeto.javafxmaven.modelo.Usuario;
import org.projeto.javafxmaven.repository.DivulgarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DivulgarService {

    @Autowired
    private DivulgarRepository repo;

    @Autowired
    private TrabalhadorService trabalhadorService;

    @Autowired
    private UsuarioService usuarioService; // ✅ Adicionado

    public Divulgar salvarServico(Divulgar divulgar) {
        divulgar.setDataPublicacao(LocalDateTime.now());

        Trabalhador logado = trabalhadorService.getUsuarioLogado();
        if (logado == null) {
            throw new IllegalStateException("Usuário não está logado.");
        }

        divulgar.setTrabalhador(logado);
        return repo.save(divulgar);
    }

    public Divulgar salvarServico(Usuario usuario, Divulgar divulgar) {
        if (usuario == null) {
            throw new IllegalStateException("Usuário logado não encontrado.");
        }

        divulgar.setUsuario(usuario);
        divulgar.setDataPublicacao(LocalDateTime.now());

        return repo.save(divulgar);
    }

    public List<Divulgar> listarServicos() {
        return repo.findAllByOrderByDataPublicacaoDesc();
    }

    public List<Divulgar> buscarServicosDoLogado() {
        Pessoa usuario = usuarioService.getUsuarioLogado();
        Trabalhador trabalhador = trabalhadorService.getUsuarioLogado();

        if (usuario != null) {
            return repo.findByUsuarioOrderByDataPublicacaoDesc((Usuario) usuario);
        } else if (trabalhador != null) {
            return repo.findByTrabalhadorOrderByDataPublicacaoDesc(trabalhador);
        }
        return List.of();
    }

    public void removerServicosSemUsuario() {
        List<Divulgar> todos = repo.findAll();
        for (Divulgar d : todos) {
            if (d.getTrabalhador() == null && d.getUsuario() == null) {
                repo.delete(d);
                System.out.println("Removido: " + d.getTitulo());
            }
        }
    }

    @PostConstruct
    public void init() {
        this.removerServicosSemUsuario();
    }
}
