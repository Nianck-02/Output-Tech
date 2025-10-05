package org.projeto.javafxmaven.Config;


import org.projeto.javafxmaven.modelo.Pessoa;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {
    private Pessoa pessoaLogada;

    public Pessoa getPessoaLogada() {
        return pessoaLogada;
    }

    public void setPessoaLogada(Pessoa pessoaLogada) {
        this.pessoaLogada = pessoaLogada;
    }
}


