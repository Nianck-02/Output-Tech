package org.projeto.javafxmaven.Config;

import org.projeto.javafxmaven.modelo.Mensagem;
import org.projeto.javafxmaven.modelo.Usuario;
import org.projeto.javafxmaven.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    // Envia uma nova mensagem entre usuários
    public Mensagem enviarMensagem(Usuario remetente, Usuario destinatario, String conteudo) {
        Mensagem mensagem = new Mensagem();
        mensagem.setRemetente(remetente);
        mensagem.setDestinatario(destinatario);
        mensagem.setConteudo(conteudo);
        mensagem.setDataHora(LocalDateTime.now());

        return mensagemRepository.save(mensagem);
    }


    // Lista as mensagens trocadas entre dois usuários em ordem cronológica
    public List<Mensagem> listarConversasEntre(Usuario usuario1, Usuario usuario2) {
        return mensagemRepository.findByRemetenteAndDestinatarioOrDestinatarioAndRemetenteOrderByDataHoraAsc(
                usuario1, usuario2, usuario1, usuario2
        );
    }

    // Lista todos os usuários com quem o usuário atual já conversou
    public List<Usuario> listarUsuariosComQuemConversou(Long usuarioId) {
        List<Usuario> remetentes = mensagemRepository.buscarRemetentes(usuarioId);
        List<Usuario> destinatarios = mensagemRepository.buscarDestinatarios(usuarioId);

        List<Usuario> todos = new ArrayList<>();
        todos.addAll(remetentes);
        todos.addAll(destinatarios);

        // Remove duplicatas
        return todos.stream()
                .distinct()
                .collect(Collectors.toList());
    }

}
