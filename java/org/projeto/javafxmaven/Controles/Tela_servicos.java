package org.projeto.javafxmaven.Controles;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.projeto.javafxmaven.Config.*;
import org.projeto.javafxmaven.modelo.Divulgar;
import org.projeto.javafxmaven.modelo.Servico;
import org.projeto.javafxmaven.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

@Component
public class Tela_servicos implements Initializable {

    private static Tela_servicos instance;




    @FXML private VBox vboxlistaServicos;

    @FXML private Button btnconta;
    @FXML private Button btncontacts;
    @FXML private Button btncstatistichs;
    @FXML private Button btnincio;
    @FXML private Button btnspeciality;
    @FXML private Label lbltipotrabalho;

    @Autowired private DivulgarService divulgarService;

    @Autowired private SpringFXMLLoader springFXMLLoader;

    public Tela_servicos() {
        instance = this;
    }

    public static Tela_servicos getInstance() {
        return instance;
    }

    public VBox getVboxListaServicos() {
        return vboxlistaServicos;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        atualizarLista();
    }

    public void atualizarLista() {
        vboxlistaServicos.getChildren().clear();
        for (Divulgar divulgar : divulgarService.listarServicos()) {
            Node card = criarCard(divulgar);
            vboxlistaServicos.getChildren().add(card);
        }
    }

    private String tempoDecorrido(LocalDateTime dataPublicacao) {
        LocalDateTime agora = LocalDateTime.now();
        Duration duracao = Duration.between(dataPublicacao, agora);

        long segundos = duracao.getSeconds();
        long minutos = duracao.toMinutes();
        long horas = duracao.toHours();
        long dias = duracao.toDays();

        if (segundos < 60) {
            return "há poucos segundos";
        } else if (minutos < 60) {
            return "há " + minutos + " minutos";
        } else if (horas < 24) {
            return "há " + horas + " horas";
        } else {
            return "há " + dias + " dias";
        }
    }

    private Node criarCard(Divulgar d) {
        // Título
        Label titulo = new Label(d.getTitulo());
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Linha de informações secundárias
        Label publicado = new Label("Publicado: " + tempoDecorrido(d.getDataPublicacao()));
        Label status = new Label("Recebe por: " + d.getTipo());

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox infoBox = new HBox(15, publicado, espaco, status);
        infoBox.setStyle("-fx-font-size: 13px;");
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // Descrição com quebra de linha
        Label descricao = new Label("Descrição:\n" + d.getDescricao());
        descricao.setWrapText(true);
        descricao.setStyle("-fx-font-size: 14px;");
        descricao.setMaxWidth(500);
        descricao.setMinHeight(Region.USE_PREF_SIZE);

        // Categoria e habilidades
        Label categoria = new Label("Categoria: " + d.getCategoria());
        categoria.setStyle("-fx-font-size: 14px;");

        // Exemplo: se quiser adicionar habilidades posteriormente
        Label habilidades = new Label("Habilidades: " + d.getHabilidades());
        habilidades.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox textoBox = new VBox(5, descricao, categoria, habilidades);
        textoBox.setAlignment(Pos.CENTER_LEFT);

        // Botão de ação
        Button btnEditar = new Button("Entrar em contato");
        btnEditar.setStyle("""
        -fx-background-color: transparent;
        -fx-border-color: #3b3b3b;
        -fx-padding: 10px 10px;
        -fx-border-radius: 5px;
        -fx-font-size: 12px;
        -fx-cursor: hand;
    """);


        btnEditar.setOnAction(e -> {
            try {
                Usuario autor = d.getUsuario(); // pega o autor da publicação

                if (autor == null) {
                    System.out.println("⚠ Serviço não possui um usuário associado.");
                    return;
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Tela_chat_Usuario.fxml"));
                loader.setControllerFactory(AppConfig.getContext()::getBean);

                Parent root = loader.load();
                Tela_chat_Usuario controller = loader.getController();

                // Define o usuário atual e o contato
                Usuario usuarioLogado = (Usuario) AppConfig.getContext().getBean(UsuarioService.class).getUsuarioLogado();

                controller.setUsuarioAtual(usuarioLogado);
                controller.carregarMensagens(autor);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Chat com " + autor.getNome());
                stage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });



        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox botaoBox = new HBox(spacer, btnEditar);
        botaoBox.setAlignment(Pos.CENTER_RIGHT);

        // Card final
        VBox card = new VBox(12, titulo, infoBox, textoBox, botaoBox);
        card.setPadding(new Insets(35, 30, 35, 30));
        card.setStyle("""
        -fx-background-color: #ffffff;
        -fx-border-color: #888;
        -fx-border-radius: 10px;
        -fx-background-radius: 10px;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);
    """);

        // Tamanho fixo igual ao outro
        card.setPrefWidth(600);
        card.setMinWidth(600);
        card.setMaxWidth(800);
        card.setMinHeight(300);
        card.setPrefHeight(Region.USE_COMPUTED_SIZE);

        return card;
    }

    @FXML private void tela_publicar() {
        carregarTela("/view/Tela_divulgar.fxml", btnspeciality);
    }
    @FXML private void tela_conta() {
        carregarTela("/view/Tela_conta_Trabalhador.fxml", btnconta);
    }
    @FXML private void tela_statisticas() {
        carregarTela("/view/Tela_estatistica_trabalhador.fxml", btncstatistichs);
    }
    @FXML private void tela_chat() {
        carregarTela("/view/Tela_chat_Usuario.fxml", btncontacts);
    }
    @FXML private void tela_inicio() {
        carregarTela("/view/Tela_inicial_Trabalhador.fxml", btnincio);
    }

    private void carregarTela(String caminhoFXML, Button origem) {
        try {
            FXMLLoader loader = springFXMLLoader.load(caminhoFXML);
            Parent root = loader.load();
            origem.getScene().setRoot(root);

            FadeTransition fadeTransition = new FadeTransition(javafx.util.Duration.millis(300), root);
            fadeTransition.setFromValue(0.8);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao iniciar a tela: " + caminhoFXML);
        }
    }


}
