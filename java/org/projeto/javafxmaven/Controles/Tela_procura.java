package org.projeto.javafxmaven.Controles;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.projeto.javafxmaven.Config.ServicoService;
import org.projeto.javafxmaven.modelo.Servico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

@Component
public class Tela_procura implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        atualizarLista();

    }


    private static Tela_procura instance;

    @FXML
    private VBox vboxlistaServicos;

    public VBox getVboxListaServicos() {
        return vboxlistaServicos;
    }

    @Autowired
    private ServicoService servicoService;


    public Tela_procura() {
        instance = this;
    }

    public static Tela_procura getInstance() {
        return instance;
    }

    public void atualizarLista() {
        vboxlistaServicos.getChildren().clear();
        for (Servico servico : servicoService.listarServicos()) {
            Node card = criarCardServico(servico);
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


    private Node criarCardServico(Servico s) {
        // Título
        Label titulo = new Label(s.getTitulo());
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Linha de informações secundárias
        Label publicado = new Label("Publicado: " + tempoDecorrido(s.getDataPublicacao()));
        Label contatos = new Label("Entraram em contato: 5");
        Label tipo = new Label("Trabalha com: " + s.getTipo());

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox infoBox = new HBox(15, publicado, contatos, espaco, tipo);
        infoBox.setStyle("-fx-font-size: 13px;");
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // Descrição com quebra de linha
        Label descricao = new Label("Descrição:\n" + s.getDescricao());
        descricao.setWrapText(true);
        descricao.setStyle("-fx-font-size: 14px;");
        descricao.setMaxWidth(500); // força quebra
        descricao.setMinHeight(Region.USE_PREF_SIZE); // garante ajuste vertical

        // Categoria e Habilidades
        Label categoria = new Label("Categoria: " + s.getCategoria());
        categoria.setStyle("-fx-font-size: 14px;");

        Label habilidades = new Label("Habilidades: " + s.getHabilidades());
        habilidades.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox textoBox = new VBox(5, descricao, categoria, habilidades);
        textoBox.setAlignment(Pos.CENTER_LEFT);

        // Botão
        Button btnContato = new Button("Entrar em contato");
        btnContato.setStyle("""
        -fx-background-color: transparent;
        -fx-border-color: #3b3b3b;
        -fx-padding: 10px 10px;
        -fx-border-radius: 5px;
        -fx-font-size: 12px;
        -fx-cursor: hand;
    """);

        btnContato.setOnAction(e -> {
            System.out.println("Entrando em contato com: " + s.getTitulo());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox botaoBox = new HBox(spacer, btnContato);
        botaoBox.setAlignment(Pos.CENTER_RIGHT);

        // Card final
        VBox card = new VBox(10, titulo, infoBox, textoBox, botaoBox);
        card.setPadding(new Insets(35, 30, 35, 30));
        card.setStyle("""
        -fx-background-color: #ffffff;
        -fx-border-color: #888;
        -fx-border-radius: 10px;
        -fx-background-radius: 10px;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);
    """);

        // Tamanho fixo ajustado
        card.setPrefWidth(600);
        card.setMinWidth(600);
        card.setMaxWidth(800);
        card.setMinHeight(300); // antes era 300
        card.setPrefHeight(Region.USE_COMPUTED_SIZE);

        return card;
    }


}
