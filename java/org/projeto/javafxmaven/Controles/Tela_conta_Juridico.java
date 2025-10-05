package org.projeto.javafxmaven.Controles;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.projeto.javafxmaven.Config.JuridicoService;
import org.projeto.javafxmaven.Config.SpringFXMLLoader;
import org.projeto.javafxmaven.Config.TrabalhadorService;
import org.projeto.javafxmaven.Config.UsuarioService;
import org.projeto.javafxmaven.modelo.Juridico;
import org.projeto.javafxmaven.modelo.Trabalhador;
import org.projeto.javafxmaven.modelo.Usuario;
import org.projeto.javafxmaven.repository.JuridicoRepository;
import org.projeto.javafxmaven.repository.PessoaRepository;
import org.projeto.javafxmaven.repository.TrabalhadorRepository;
import org.projeto.javafxmaven.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

@Component
public class Tela_conta_Juridico implements Initializable {
    @FXML private Button btnaccout, btnalterar, btncancelar, btncontacts, btndashboard, btnhelp, btnpublicar, btnsair, btnservices, btnstatistichs;
    @FXML private ComboBox<String> cmbUF;
    @FXML private MenuButton cmbtipo;
    @FXML private PasswordField pswsenha;
    @FXML private TextField txtcidade, txtcnpj, txtemail, txtname;

    private static final byte[] CHAVE_AES = "2312024202526123".getBytes();

    @Autowired private SpringFXMLLoader springFXMLLoader;
    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private JuridicoRepository juridicoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TrabalhadorRepository trabalhadorRepository;
    @Autowired private JuridicoService juridicoService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbtipo.getItems().clear();
        adicionarOpcoesTipo();
        cmbUF.getItems().addAll("AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

        try {
            carregarDadosUsuario();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        txtcnpj.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !isCnpjValido(txtcnpj.getText())) {
                mostrarAlerta("CNPJ inválido", "Digite um CNPJ válido!");
                txtcnpj.setText("");
            }
        });
        txtcnpj.setOnKeyReleased(this::aplicarMascaraCNPJ);
    }

    private boolean isCnpjValido(String cnpj) {
        cnpj = cnpj.replaceAll("[^\\d]", ""); // Remove pontos, barras e traços

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) return false;

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        try {
            int soma = 0;
            for (int i = 0; i < 12; i++)
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];

            int dig1 = soma % 11;
            dig1 = (dig1 < 2) ? 0 : 11 - dig1;

            soma = 0;
            for (int i = 0; i < 13; i++)
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];

            int dig2 = soma % 11;
            dig2 = (dig2 < 2) ? 0 : 11 - dig2;

            return cnpj.charAt(12) == Character.forDigit(dig1, 10) &&
                    cnpj.charAt(13) == Character.forDigit(dig2, 10);
        } catch (Exception e) {
            return false;
        }
    }

    private void adicionarOpcoesTipo() {
        String[] tipos = {"Jurídico","Freelancer", "Meio periodo"};
        for (String tipo : tipos) {
            MenuItem item = new MenuItem(tipo);
            item.setOnAction(e -> cmbtipo.setText(tipo));
            cmbtipo.getItems().add(item);
        }
    }

    private boolean cnpjJaCadastrado(String cnpj) {
        return juridicoRepository.findByCnpj(cnpj).isPresent() ;
    }

    public String validarGmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$") ? "OK" : "O e-mail deve ser um Gmail válido, exemplo: usuario@gmail.com.";
    }

    private boolean emailJaCadastrado(String email) {
        return pessoaRepository.findByEmail(email).isPresent() || trabalhadorRepository.findByEmail(email).isPresent();
    }

    private void carregarDadosUsuario() throws Exception {
        Juridico juridico = juridicoService.getUsuarioLogado();
        if (juridico != null) {
            txtname.setText(juridico.getNome());
            txtemail.setText(juridico.getEmail());
            txtcnpj.setText(juridico.getCnpj());
            cmbUF.setValue(juridico.getUf());
            txtcidade.setText(juridico.getCidade());
            pswsenha.setText(descriptografar(juridico.getSenha()));
            cmbtipo.setText(juridico.getTipo());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void aplicarMascaraCNPJ(javafx.scene.input.KeyEvent event) {
        String texto = txtcnpj.getText().replaceAll("[^0-9]", "");
        if (texto.length() > 14) texto = texto.substring(0, 14);

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            if (i == 2 || i == 5) formatted.append(".");
            else if (i == 8) formatted.append("/");
            else if (i == 12) formatted.append("-");
            formatted.append(texto.charAt(i));
        }

        txtcnpj.setText(formatted.toString());
        txtcnpj.positionCaret(formatted.length());
    }

    @FXML
    private void cancelar() {
        try {
            carregarDadosUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void alterardados() throws Exception {
        String nome = txtname.getText();
        String email = txtemail.getText();
        String cnpj = txtcnpj.getText();
        String cidade = txtcidade.getText();
        String uf = cmbUF.getValue();
        String tipo = cmbtipo.getText();
        String senha = pswsenha.getText();

        if (nome.isEmpty() || email.isEmpty() || cnpj.isEmpty() || cidade.isEmpty() || uf == null || tipo == null || senha.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos!");
            return;
        }

        if (!"OK".equals(validarGmail(email))) {
            mostrarAlerta("Erro", validarGmail(email));
            return;
        }

        Juridico juridico = juridicoService.getUsuarioLogado();
        if (juridico != null) {
            if (!email.equals(juridico.getEmail()) && emailJaCadastrado(email)) {
                mostrarAlerta("Erro", "E-mail já cadastrado. Tente outro.");
                return;
            }
            if (!cnpj.equals(juridico.getCnpj()) && cnpjJaCadastrado(cnpj)) {
                mostrarAlerta("Erro", "CNPJ já cadastrado. Tente outro.");
                return;
            }

            juridico.setNome(nome);
            juridico.setEmail(email);
            juridico.setCnpj(cnpj);
            juridico.setUf(uf);
            juridico.setCidade(cidade);
            juridico.setSenha(CriptografiaAES.criptografar(senha));

            if (juridicoService.salvar(juridico)) {
                mostrarAlerta("Sucesso", "Seus dados foram atualizados.");
            } else {
                mostrarAlerta("Erro", "Ocorreu um erro ao salvar os dados.");
            }
        } else {
            mostrarAlerta("Erro", "Nenhum usuário logado.");
        }
    }

    public static class CriptografiaAES {
        private static final String ALGO = "AES";
        private static final byte[] chave = "2312024202526123".getBytes();

        public static String criptografar(String valor) throws Exception {
            SecretKeySpec keySpec = new SecretKeySpec(chave, ALGO);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(valor.getBytes()));
        }
    }

    private String descriptografar(String valorCriptografado) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(CHAVE_AES, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(valorCriptografado)));
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

    @FXML private void tela_publicar() { carregarTela("/view/Tela_publicar.fxml", btnservices); }
    @FXML private void tela_dashborad() { carregarTela("/view/Tela_inicial_Usuario.fxml", btndashboard); }
    @FXML private void tela_statisticas() { carregarTela("/view/Tela_estatistica_usuario.fxml", btnstatistichs); }
    @FXML private void tela_chat() { carregarTela("/view/Tela_chat_Usuario.fxml", btncontacts); }
    @FXML private void tela_services() { carregarTela("/view/Tela_procura.fxml", btnservices); }
}