package org.projeto.javafxmaven.Config;

import org.projeto.javafxmaven.modelo.Juridico;
import org.projeto.javafxmaven.modelo.Pessoa;
import org.projeto.javafxmaven.repository.JuridicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JuridicoService {

    @Autowired
    private JuridicoRepository juridicoRepository;

    @Autowired
    private PessoaService pessoaService; // ✅ Centraliza login

    public void setUsuarioLogado(Juridico juridico) {
        pessoaService.setPessoaLogada(juridico); // ✅ Centralizado
    }

    public Juridico getUsuarioLogado() {
        Pessoa pessoa = pessoaService.getPessoaLogada(); // Busca global
        if (pessoa instanceof Juridico) {
            return (Juridico) pessoa;
        }
        return null;
    }

    public boolean salvar(Juridico juridico) {
        try {
            juridicoRepository.save(juridico);
            setUsuarioLogado(juridico); // ✅ define como logado
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao salvar jurídico: " + e.getMessage());
            return false;
        }
    }
}
