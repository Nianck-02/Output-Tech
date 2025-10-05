package org.projeto.javafxmaven.Controles;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.projeto.javafxmaven.Config.SpringFXMLLoader;
import org.projeto.javafxmaven.modelo.Juridico;
import org.projeto.javafxmaven.repository.JuridicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

@Component
public class Tela_cadastro_Juridico implements Initializable {

    @FXML private Button btncadastrar, btnlogin, btnpessoafisica, btnMostrarSenha;
    @FXML private CheckBox chcktermosdeuso;
    @FXML private ComboBox<String> cmbUF;
    @FXML private PasswordField pswconfsenha, pswsenha;
    @FXML private TextField txtcidade, txtcnpj, txtemail, txtnome;

    private boolean senhaVisivel = false;

    @Autowired
    private JuridicoRepository juridicoRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbUF.getItems().addAll("AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
                "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO",
                "RR", "SC", "SP", "SE", "TO");

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

    private boolean emailJaCadastrado(String email) {
        return juridicoRepository.findByEmail(email).isPresent();
    }

    private boolean cnpjJaCadastrado(String cnpj) {
        return juridicoRepository.findByCnpj(cnpj).isPresent();
    }
    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @FXML
    private void telalogin() {
        try {
            FXMLLoader loader = springFXMLLoader.load("/view/Tela_login.fxml");
            Parent root = loader.load();
            btnlogin.getScene().setRoot(root);
            FadeTransition ft = new FadeTransition(Duration.millis(0), root);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir a tela de login.");
        }
    }

    @FXML
    private void telapessoacomum() {
        try {
            FXMLLoader loader = springFXMLLoader.load("/view/Tela_cadastro_Pessoa.fxml");
            Parent root = loader.load();
            btnlogin.getScene().setRoot(root);
            FadeTransition ft = new FadeTransition(Duration.millis(0), root);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir a tela de login.");
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
    private void cadjuridico() {
        String nome = txtnome.getText();
        String email = txtemail.getText();
        String cnpj = txtcnpj.getText();
        String cidade = txtcidade.getText();
        String uf = cmbUF.getValue();
        String senha = pswsenha.getText();
        String confirmarSenha = pswconfsenha.getText();

        if (nome.isEmpty() || email.isEmpty() || cnpj.isEmpty() || cidade.isEmpty() || uf == null || senha.isEmpty() || confirmarSenha.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos!");
            return;
        }
        if (!isCnpjValido(cnpj)) {
            mostrarAlerta("Erro", "CNPJ inválido. Verifique e tente novamente.");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            mostrarAlerta("Erro", "As senhas não coincidem!");
            return;
        }

        String validacaoSenha = validarSenhaComMensagem(senha);
        if (!"OK".equals(validacaoSenha)) {
            mostrarAlerta("Erro", validacaoSenha);
            return;
        }

        String validacaoEmail = validarGmail(email);
        if (!"OK".equals(validacaoEmail)) {
            mostrarAlerta("Erro", validacaoEmail);
            return;
        }

        if (!chcktermosdeuso.isSelected()) {
            mostrarAlerta("Erro", "Aceite os termos de uso para continuar.");
            return;
        }

        if (emailJaCadastrado(email)) {
            mostrarAlerta("Erro", "E-mail já cadastrado. Tente outro.");
            return;
        }

        if (cnpjJaCadastrado(cnpj)) {
            mostrarAlerta("Erro", "CNPJ já cadastrado. Tente outro.");
            return;
        }

        try {
            String senhaCriptografada = CriptografiaAES.criptografar(senha);
            Juridico juridico = new Juridico();
            juridico.setNome(nome);
            juridico.setEmail(email);
            juridico.setCnpj(cnpj);
            juridico.setUf(uf);
            juridico.setCidade(cidade);
            juridico.setSenha(senhaCriptografada);
            juridico.setTipo("Jurídico");

            juridicoRepository.save(juridico);


            mostrarAlerta("Sucesso", "Cadastro realizado com sucesso!");
            telalogin();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao cadastrar: " + e.getMessage());
        }
    }

    public String validarSenhaComMensagem(String senha) {
        if (senha.length() < 8) return "A senha deve ter pelo menos 8 caracteres.";
        if (!senha.matches(".*[A-Z].*")) return "A senha deve conter pelo menos uma letra maiúscula.";
        if (!senha.matches(".*[a-z].*")) return "A senha deve conter pelo menos uma letra minúscula.";
        if (!senha.matches(".*[0-9].*")) return "A senha deve conter pelo menos um número.";
        if (!senha.matches(".*[@$!%*?&].*")) return "A senha deve conter pelo menos um caractere especial (@$!%*?&).";
        return "OK";
    }

    public String validarGmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$") ? "OK" : "O e-mail deve ser um Gmail válido, exemplo: usuario@gmail.com.";
    }

    public static class CriptografiaAES {
        private static final String ALGO = "AES";
        private static final byte[] chave = "2312024202526123".getBytes();

        public static String criptografar(String valor) throws Exception {
            SecretKeySpec keySpec = new SecretKeySpec(chave, ALGO);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(valor.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        }

        public static String descriptografar(String valorCriptografado) throws Exception {
            SecretKeySpec keySpec = new SecretKeySpec(chave, ALGO);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(valorCriptografado);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        }
    }
}
