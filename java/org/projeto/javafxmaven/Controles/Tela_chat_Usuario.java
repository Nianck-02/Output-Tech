package org.projeto.javafxmaven.Controles;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.projeto.javafxmaven.Config.MensagemService;
import org.projeto.javafxmaven.Config.SpringFXMLLoader;
import org.projeto.javafxmaven.Config.UsuarioService;
import org.projeto.javafxmaven.modelo.Mensagem;
import org.projeto.javafxmaven.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class Tela_chat_Usuario implements Initializable {

    @FXML private Button btnaccount;
    @FXML private Button btncontacts;
    @FXML private Button btndashboard;
    @FXML private Button btnhelp;
    @FXML private Button btnpublicar;
    @FXML private Button btnservice;
    @FXML private Button btnstatischs;
    @FXML private Button btnmandarmsg;

    @FXML private TextField txtchat;
    @FXML private TextField txtpesquisar;

    @FXML private VBox vboxcontas;
    @FXML private VBox vboxnotification;
    @FXML private VBox listaContatosVBox;
    @FXML private VBox vboxMensagens;
    @FXML private VBox vboxperfil;

    @Autowired private UsuarioService usuarioService;
    @Autowired private MensagemService mensagemService;

    private Usuario usuarioAtual;
    private Usuario usuarioSelecionado;

    @Autowired private SpringFXMLLoader springFXMLLoader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // A inicialização pode carregar os contatos se o usuário atual estiver definido
    }

    public void setUsuarioAtual(Usuario usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
        carregarContatos();
        atualizarPerfilUsuarioAtual();
    }

    private void atualizarPerfilUsuarioAtual() {
        if (usuarioAtual == null) return;

        vboxperfil.getChildren().clear();

        Label nome = new Label(usuarioAtual.getNome());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label status = new Label("Online");
        status.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");

        VBox info = new VBox(nome, status);
        info.setSpacing(5);
        info.setStyle("-fx-padding: 10;");

        vboxperfil.getChildren().add(info);
    }

    public void carregarContatos() {
        listaContatosVBox.getChildren().clear();
        List<Usuario> conversas = mensagemService.listarUsuariosComQuemConversou(usuarioAtual.getId());

        for (Usuario contato : conversas) {
            HBox cardContato = criarCardContato(contato);
            cardContato.setOnMouseClicked(e -> carregarMensagens(contato));
            listaContatosVBox.getChildren().add(cardContato);
        }
    }

    private HBox criarCardContato(Usuario contato) {
        Label nome = new Label(contato.getNome());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label ultimaMsg = new Label("ultima mensagem");
        ultimaMsg.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        VBox texts = new VBox(nome, ultimaMsg);
        texts.setSpacing(3);

        HBox card = new HBox(10, texts);
        card.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 6px; -fx-background-radius: 6px;");
        return card;
    }
    public void carregarMensagens(Usuario contato) {
        if (contato == null) {
            System.err.println("Erro: contato é null");
            return;
        }

        this.usuarioSelecionado = contato;
        vboxMensagens.getChildren().clear();

        // Exibir o perfil do usuário (foto e nome)
        exibirPerfilContato(contato);

        // Carregar as mensagens entre os usuários
        List<Mensagem> mensagens = mensagemService.listarConversasEntre(usuarioAtual, contato);
        for (Mensagem m : mensagens) {
            boolean souEu = m.getRemetente().getId().equals(usuarioAtual.getId());
            Node node = criarMensagem(m, souEu);
            vboxMensagens.getChildren().add(node);
        }

    }
    private void exibirPerfilContato(Usuario contato) {
        // Criando um HBox para exibir apenas o nome do contato
        HBox perfilBox = new HBox(10);

        // Nome do contato
        Label nomeContato = new Label(contato.getNome());
        nomeContato.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Adicionando apenas o nome ao perfilBox
        perfilBox.getChildren().add(nomeContato);

        // Definindo o estilo do perfilBox
        perfilBox.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // Adiciona o perfilBox ao topo do vboxMensagens
        vboxMensagens.getChildren().add(0, perfilBox);  // Adiciona o perfil do contato no topo do chat
    }


    @FXML
    private void enviarMensagem() {
        String conteudo = txtchat.getText();
        if (conteudo.isBlank() || usuarioSelecionado == null) return;

        // Criando uma nova mensagem
        Mensagem nova = mensagemService.enviarMensagem(usuarioAtual, usuarioSelecionado, conteudo);

        // Adicionando a mensagem enviada ao chat
        Node novaMsg = criarMensagem(nova, true); // 'true' porque a mensagem é enviada por você
        vboxMensagens.getChildren().add(novaMsg);

        // Limpa o campo de texto após enviar a mensagem
        txtchat.clear();
    }

    private Node criarMensagem(Mensagem m, boolean souEu) {
        // Estilo das mensagens enviadas por você
        String corFundo = souEu ? "#dcf8c6" : "#ffffff";  // Verde claro para mensagens enviadas por você
        String alinhamento = souEu ? "CENTER_RIGHT" : "CENTER_LEFT";

        Label texto = new Label(m.getConteudo());
        texto.setStyle("-fx-background-color: " + corFundo + ";"
                + "-fx-padding: 8px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #ccc;");
        texto.setWrapText(true);
        texto.setMaxWidth(300);

        HBox box = new HBox(texto);
        box.setAlignment(souEu ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT); // Alinha à direita para você e à esquerda para o outro
        return box;
    }

    private void carregarTela(String caminhoFXML, Button origem) {
        try {
            FXMLLoader loader = springFXMLLoader.load(caminhoFXML);
            Parent root = loader.load();
            origem.getScene().setRoot(root);
            FadeTransition ft = new FadeTransition(Duration.millis(300), root);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao iniciar a tela: " + caminhoFXML);
        }
    }

    @FXML private void tela_publicar() { carregarTela("/view/Tela_publicar.fxml", btnpublicar); }
    @FXML private void tela_dashborad() { carregarTela("/view/Tela_inicial_Trabalhador.fxml", btndashboard); }
    @FXML private void tela_statisticas() { carregarTela("/view/Tela_estatistica_trabalhador.fxml", btnstatischs); }
    @FXML private void tela_conta() { carregarTela("/view/Tela_conta_Usuario.fxml", btnaccount); }
    @FXML private void tela_services() { carregarTela("/view/Tela_servicos.fxml", btnservice);}

}
