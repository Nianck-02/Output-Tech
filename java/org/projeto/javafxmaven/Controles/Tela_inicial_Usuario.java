package org.projeto.javafxmaven.Controles;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Duration;
import org.projeto.javafxmaven.Config.SpringFXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class Tela_inicial_Usuario implements Initializable {

    @Autowired private SpringFXMLLoader springFXMLLoader;

    @FXML private Button btnaccount;
    @FXML private Button btncontacts;
    @FXML private Button btndashboard;
    @FXML private Button btnpublicar;
    @FXML private Button btnservices;
    @FXML private Button btnstatistichs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Nada necessário no momento
    }

    @FXML
    private void tela_publicar() {
        String tela;
        if (Sessao.SessaoUsuario.isJuridico()) {
            tela = "/view/Tela_publicar.fxml"; // Pode ser específica para Jurídico
        } else {
            tela = "/view/Tela_publicar.fxml"; // Diferencie se houver layout diferente
        }
        carregarTela(tela, btnpublicar);
    }

    @FXML
    private void tela_conta() {
        String telaConta;
        if (Sessao.SessaoUsuario.isJuridico()) {
            telaConta = "/view/Tela_conta_Juridico.fxml";
        } else {
            telaConta = "/view/Tela_conta_Usuario.fxml";
        }
        carregarTela(telaConta, btnaccount);
    }

    @FXML
    private void tela_statisticas() {
        String telaEstatisticas;
        if (Sessao.SessaoUsuario.isJuridico()) {
            telaEstatisticas = "/view/Tela_estatistica_usuario.fxml";
        } else {
            telaEstatisticas = "/view/Tela_estatistica_usuario.fxml";
        }
        carregarTela(telaEstatisticas, btnstatistichs);
    }

    @FXML
    private void tela_chat() {
        String telaChat;
        if (Sessao.SessaoUsuario.isJuridico()) {
            telaChat = "/view/Tela_chat_Usuario.fxml";
        } else {
            telaChat = "/view/Tela_chat_Usuario.fxml";
        }
        carregarTela(telaChat, btncontacts);
    }

    @FXML
    private void tela_services() {
        String telaServices;
        if (Sessao.SessaoUsuario.isJuridico()) {
            telaServices = "/view/Tela_servicos.fxml";
        } else {
            telaServices = "/view/Tela_servicos.fxml"; // ← use outra se layout for diferente
        }
        carregarTela(telaServices, btnservices);
    }

    private void carregarTela(String caminhoFXML, Button origem) {
        try {
            FXMLLoader loader = springFXMLLoader.load(caminhoFXML);
            Parent root = loader.load();
            origem.getScene().setRoot(root);

            FadeTransition fade = new FadeTransition(Duration.millis(300), root);
            fade.setFromValue(0.8);
            fade.setToValue(1.0);
            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a tela: " + caminhoFXML);
        }
    }
}
