package org.projeto.javafxmaven.Config;

import org.projeto.javafxmaven.modelo.Pessoa;
import org.projeto.javafxmaven.modelo.Usuario;
import org.projeto.javafxmaven.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private PessoaRepository usuarioRepository;

    @Autowired
    private PessoaService pessoaService; // ✅ Centraliza o usuário logado

    public void setUsuarioLogado(Pessoa pessoa) {
        pessoaService.setPessoaLogada(pessoa); // usa o serviço centralizado
    }

    public Pessoa getUsuarioLogado() {
        return pessoaService.getPessoaLogada(); // usa o serviço centralizado
    }

    public boolean salvar(Usuario usuario) {
        try {
            usuarioRepository.save(usuario);
            pessoaService.setPessoaLogada(usuario); // atualiza o logado global
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            return false;
        }
    }

    public Pessoa buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public List<Usuario> listarTodosUsuarios() {
        return (List<Usuario>) usuarioRepository.findAll();
    }
}
