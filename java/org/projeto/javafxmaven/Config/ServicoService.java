package org.projeto.javafxmaven.Config;

import org.projeto.javafxmaven.modelo.Servico;
import org.projeto.javafxmaven.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository repo;

    public Servico salvarServico(Servico servico) {
        servico.setDataPublicacao(LocalDateTime.now());
        return repo.save(servico);
    }

    public List<Servico> listarServicos() {
        return repo.findAllByOrderByDataPublicacaoDesc();
    }
}
