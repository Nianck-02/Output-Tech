package org.projeto.javafxmaven.Controles;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.projeto.javafxmaven.Config.SpringFXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class Tela_inicial_Trabalhador implements Initializable {

    @FXML private Button btnconta;
    @FXML private Button btncontacts;
    @FXML private Button btncstatistichs;
    @FXML private Button btndashboard;
    @FXML private Button btnhelp;
    @FXML private Button btnservices;
    @FXML private Button btnspeciality;

    @FXML private ComboBox<?> cmbSettings;
    @FXML private Label lblcategoria;
    @FXML private Label lbltipoconta;
    @FXML private Label lbltiposervico;
    @FXML private TextField txtbarradepesquisa;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialização, se necessário
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
    @FXML private void tela_services() {
        carregarTela("/view/Tela_servicos.fxml", btnservices);
    }

    private void carregarTela(String caminhoFXML, Button origem) {
        try {
            FXMLLoader loader = springFXMLLoader.load(caminhoFXML);
            Parent root = loader.load();
            origem.getScene().setRoot(root);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), root);
            fadeTransition.setFromValue(0.8);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao iniciar a tela: " + caminhoFXML);
        }
    }
}
