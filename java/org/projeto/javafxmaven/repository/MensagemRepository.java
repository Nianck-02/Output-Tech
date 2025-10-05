package org.projeto.javafxmaven.repository;

import org.projeto.javafxmaven.modelo.Mensagem;
import org.projeto.javafxmaven.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    @Query("SELECT DISTINCT CASE " +
            "WHEN m.remetente.id = :id THEN m.destinatario " +
            "ELSE m.remetente END " +
            "FROM Mensagem m " +
            "WHERE m.remetente.id = :id OR m.destinatario.id = :id")
    List<Usuario> listarUsuariosComQuemConversou(@Param("id") Long id);


    List<Mensagem> findByRemetenteAndDestinatarioOrDestinatarioAndRemetenteOrderByDataHoraAsc(
            Usuario remetente1, Usuario destinatario1, Usuario remetente2, Usuario destinatario2
    );
    @Query("SELECT DISTINCT m.remetente FROM Mensagem m WHERE m.destinatario.id = :usuarioId")
    List<Usuario> buscarRemetentes(@Param("usuarioId") Long usuarioId);

    @Query("SELECT DISTINCT m.destinatario FROM Mensagem m WHERE m.remetente.id = :usuarioId")
    List<Usuario> buscarDestinatarios(@Param("usuarioId") Long usuarioId);

}
