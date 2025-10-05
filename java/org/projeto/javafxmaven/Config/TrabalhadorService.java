package org.projeto.javafxmaven.Config;

import org.projeto.javafxmaven.modelo.Pessoa;
import org.projeto.javafxmaven.modelo.Trabalhador;
import org.projeto.javafxmaven.repository.TrabalhadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrabalhadorService {

    @Autowired
    private TrabalhadorRepository trabalhadorRepository;

    @Autowired
    private PessoaService pessoaService; // ✅ Usado para centralizar usuário logado

    public void setUsuarioLogado(Trabalhador trabalhador) {
        pessoaService.setPessoaLogada(trabalhador); // ✅ define globalmente
    }

    public Trabalhador getUsuarioLogado() {
        Pessoa pessoa = pessoaService.getPessoaLogada(); // busca globalmente
        if (pessoa instanceof Trabalhador) {
            return (Trabalhador) pessoa;
        }
        return null;
    }

    public boolean salvar(Trabalhador trabalhador) {
        try {
            trabalhadorRepository.save(trabalhador);
            setUsuarioLogado(trabalhador); // ✅ atualiza globalmente
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao salvar trabalhador: " + e.getMessage());
            return false;
        }
    }
}
