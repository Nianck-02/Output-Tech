package org.projeto.javafxmaven.Controles;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.projeto.javafxmaven.Config.PessoaService;
import org.projeto.javafxmaven.Config.ServicoService;
import org.projeto.javafxmaven.Config.UsuarioService;
import org.projeto.javafxmaven.modelo.Pessoa;
import org.projeto.javafxmaven.modelo.Servico;
import org.projeto.javafxmaven.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

@Component
public class Tela_publicar implements Initializable {

    @FXML private Button btnaccount;
    @FXML private Button btncontacts;
    @FXML private Button btndashboard;
    @FXML private Button btnhelp;
    @FXML private Button btnpublicar;
    @FXML private Button btnservices;
    @FXML private Button btnstatistichs;
    @FXML private Button btnlimpar;

    @FXML private MenuButton cmbcategoria;
    @FXML private MenuButton cmbfeitopara;
    @FXML private MenuButton cmbmodalidade;
    @FXML private MenuButton cmbtipo;

    @FXML private TextArea txtdescricao;
    @FXML private TextField txthabilidades;
    @FXML private TextField txttitulo;
    @FXML private TextField txtvalorservico;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private PessoaService pessoaService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtvalorservico.setVisible(true); // Ativar campo
        configurarMenus();
    }

    private void configurarMenus() {
        cmbcategoria.getItems().addAll(
                new MenuItem("TI e Programação"),
                new MenuItem("Design e Multimídia"),
                new MenuItem("Tradução e Conteúdos")
        );
        cmbfeitopara.getItems().addAll(
                new MenuItem("Freelancers"),
                new MenuItem("Meio periodo")
        );
        cmbmodalidade.getItems().addAll(
                new MenuItem("Remoto"),
                new MenuItem("Presencial")
        );
        cmbtipo.getItems().addAll(
                new MenuItem("Pago por hora"),
                new MenuItem("Valor fixo")
        );

        for (MenuItem item : cmbcategoria.getItems()) {
            item.setOnAction(e -> cmbcategoria.setText(item.getText()));
        }
        for (MenuItem item : cmbfeitopara.getItems()) {
            item.setOnAction(e -> cmbfeitopara.setText(item.getText()));
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
            Usuario usuarioLogado = Sessao.SessaoUsuario.getUsuario();

            if (usuarioLogado == null) {
                mostrarAlerta("Erro", "Usuário não está logado.");
                return;
            }

            Servico servico = new Servico();
            servico.setTitulo(txttitulo.getText());
            servico.setDescricao(txtdescricao.getText());
            servico.setHabilidades(txthabilidades.getText());
            servico.setCategoria(cmbcategoria.getText());
            servico.setFeitopara(cmbfeitopara.getText());
            servico.setModalidade(cmbmodalidade.getText());
            servico.setTipo(cmbtipo.getText());

            String valorTexto = txtvalorservico.getText().replace(",", ".").trim();
            if (!valorTexto.matches("\\d+(\\.\\d+)?")) {
                mostrarAlerta("Erro", "Valor do serviço inválido.");
                return;
            }

            servico.setValor(Double.parseDouble(valorTexto));
            servico.setDataPublicacao(LocalDateTime.now());
            servico.setUsuario(usuarioLogado); // ← sem cast

            servicoService.salvarServico(servico);

            try {
                Tela_procura controller = Tela_procura.getInstance();
                if (controller != null && controller.getVboxListaServicos() != null) {
                    controller.atualizarLista();
                } else {
                    System.out.println("Tela_procura ainda não carregada.");
                }
            } catch (Exception ex) {
                System.out.println("Erro ao atualizar Tela_procura: " + ex.getMessage());
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
        cmbfeitopara.setText("Feito para");
        cmbmodalidade.setText("Modalidade");
        cmbtipo.setText("Tipo");
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
