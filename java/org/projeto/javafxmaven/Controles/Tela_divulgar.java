package org.projeto.javafxmaven.Controles;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.projeto.javafxmaven.Config.*;
import org.projeto.javafxmaven.modelo.Divulgar;
import org.projeto.javafxmaven.modelo.Servico;
import org.projeto.javafxmaven.modelo.Trabalhador;
import org.projeto.javafxmaven.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

@Component
public class Tela_divulgar implements Initializable {


    @FXML
    private Button btnaccount;
    @FXML private Button btnclients;
    @FXML private Button btndashboard;
    @FXML private Button btnhelp;
    @FXML private Button btnpublicar;
    @FXML private Button btnservices;
    @FXML private Button btnstatistichs;
    @FXML private Button btnlimpar;

    @FXML private MenuButton cmbcategoria;
    @FXML private MenuButton cmbmodalidade;
    @FXML private MenuButton cmbtipo;

    @FXML private TextArea txtdescricao;
    @FXML private TextField txthabilidades;
    @FXML private TextField txttitulo;
    @FXML private TextField txtvalorservico;

    @Autowired
    private DivulgarService divulgarService;

    @Autowired
    private TrabalhadorService trabalhadorService;

    @Autowired UsuarioService usuarioService;

    @Autowired private SpringFXMLLoader springFXMLLoader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtvalorservico.setVisible(true); // Ativar campo
        configurarMenus();
    }

    private void configurarMenus() {
        // Exemplo simples de valores — você pode carregar do banco se preferir
        cmbcategoria.getItems().addAll(
                new MenuItem("TI e Programação"),
                new MenuItem("Design e Multimídia"),
                new MenuItem("Tradução e Conteúdos")
        );
        cmbmodalidade.getItems().addAll(
                new MenuItem("Remoto"),
                new MenuItem("Presencial")
        );
        cmbtipo.getItems().addAll(
                new MenuItem("Pago por hora"),
                new MenuItem("Valor fixo")
        );

        // Pega o valor clicado
        for (MenuItem item : cmbcategoria.getItems()) {
            item.setOnAction(e -> cmbcategoria.setText(item.getText()));
        }
        for (MenuItem item : cmbmodalidade.getItems()) {
            item.setOnAction(e -> cmbmodalidade.setText(item.getText()));
        }
        for (MenuItem item : cmbtipo.getItems()){
            item.setOnAction(e -> cmbtipo.setText(item.getText()));
        }
    }
    @FXML
    private void publicarServico() {
        try {
            Divulgar divulgar = new Divulgar();
            divulgar.setTitulo(txttitulo.getText());
            divulgar.setDescricao(txtdescricao.getText());
            divulgar.setHabilidades(txthabilidades.getText());
            divulgar.setCategoria(cmbcategoria.getText());
            divulgar.setModalidade(cmbmodalidade.getText());
            divulgar.setTipo(cmbtipo.getText());
            divulgar.setValor(Double.parseDouble(txtvalorservico.getText()));
            divulgar.setDataPublicacao(LocalDateTime.now());

            // Tenta pegar tanto Trabalhador quanto Usuario
            Usuario usuarioLogado = (Usuario) usuarioService.getUsuarioLogado();
            Trabalhador trabalhadorLogado = trabalhadorService.getUsuarioLogado();

            if (usuarioLogado != null) {
                divulgar.setUsuario(usuarioLogado);
                divulgarService.salvarServico(usuarioLogado, divulgar);
            } else if (trabalhadorLogado != null) {
                divulgar.setTrabalhador(trabalhadorLogado);
                divulgarService.salvarServico(divulgar); // usa o outro método
            } else {
                throw new IllegalStateException("Usuário não está logado.");
            }

            limparCampos();
            mostrarAlerta("Sucesso", "Serviço publicado com sucesso!");

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao publicar serviço: " + e.getMessage());
            e.printStackTrace();
        }
    }




    @FXML
    private void limparCampos() {
        txttitulo.clear();
        txtdescricao.clear();
        txthabilidades.clear();
        txtvalorservico.clear();
        cmbcategoria.setText("Categoria");
        cmbmodalidade.setText("Modalidade");
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

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

    @FXML private void tela_conta() { carregarTela("/view/Tela_conta_Trabalhador.fxml", btnaccount); }
    @FXML private void tela_dashborad() { carregarTela("/view/Tela_inicial_Trabalhador.fxml", btndashboard); }
    @FXML private void tela_statisticas() { carregarTela("/view/Tela_estatistica_trabalhador.fxml", btnstatistichs); }
    @FXML private void tela_chat() { carregarTela("/view/Tela_chat_Trabalhador.fxml", btnclients); }
    @FXML private void tela_services() { carregarTela("/view/Tela_servicos.fxml", btnservices); }

}
