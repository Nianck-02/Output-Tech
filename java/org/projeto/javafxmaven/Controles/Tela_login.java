package org.projeto.javafxmaven.Controles;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.projeto.javafxmaven.Config.JuridicoService;
import org.projeto.javafxmaven.Config.SpringFXMLLoader;
import org.projeto.javafxmaven.Config.TrabalhadorService;
import org.projeto.javafxmaven.Config.UsuarioService;
import org.projeto.javafxmaven.modelo.Juridico;
import org.projeto.javafxmaven.modelo.Usuario;
import org.projeto.javafxmaven.modelo.Trabalhador;
import org.projeto.javafxmaven.repository.JuridicoRepository;
import org.projeto.javafxmaven.repository.PessoaRepository;
import org.projeto.javafxmaven.repository.TrabalhadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class Tela_login {

    @FXML private Button btnlogin, btncriarconta;
    @FXML private PasswordField pswsenha;
    @FXML private TextField txtemail;

    @Autowired private JuridicoRepository juridicoRepository;
    @Autowired private PessoaRepository pessoaFisicaRepository;
    @Autowired private TrabalhadorRepository trabalhadorRepository;
    @Autowired private UsuarioService usuarioService;
    @Autowired private TrabalhadorService trabalhadorService;
    @Autowired private JuridicoService juridicoService;

    @Autowired private SpringFXMLLoader springFXMLLoader;

    private static final byte[] CHAVE_AES = "2312024202526123".getBytes();

    @FXML
    private void realizarLogin() {
        String email = txtemail.getText();
        String senhaDigitada = pswsenha.getText();

        if (email.isEmpty() || senhaDigitada.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos!");
            return;
        }

        try {
            // Juridico
            var juridicoOpt = juridicoRepository.findByEmail(email);
            if (juridicoOpt.isPresent()) {
                Juridico juridico = juridicoOpt.get();
                if (senhaCorreta(juridico.getSenha(), senhaDigitada)) {
                    juridicoService.setUsuarioLogado(juridico);
                    Juridico logado = Sessao.SessaoUsuario.getJuridico();
                    mostrarAlerta("Sucesso", "Login jurídico realizado com sucesso!");
                    tela_inicio1();
                    return;
                }
            }

            // Pessoa Física (Usuario)
            var pessoaOpt = pessoaFisicaRepository.findByEmail(email);
            if (pessoaOpt.isPresent()) {
                Usuario pessoa = (Usuario) pessoaOpt.get();
                if (senhaCorreta(pessoa.getSenha(), senhaDigitada)) {
                    usuarioService.setUsuarioLogado(pessoa);
                    Usuario logado = Sessao.SessaoUsuario.getUsuario();
                    mostrarAlerta("Sucesso", "Login de usuário realizado com sucesso!");
                    abrirTelaInicialDinamicamente(pessoa); // <-- aqui a lógica de decisão
                    return;
                }
            }

            // Trabalhador
            var trabalhadorOpt = trabalhadorRepository.findByEmail(email);
            if (trabalhadorOpt.isPresent()) {
                Trabalhador trabalhador = trabalhadorOpt.get();
                if (senhaCorreta(trabalhador.getSenha(), senhaDigitada)) {
                    trabalhadorService.setUsuarioLogado(trabalhador);
                    Trabalhador logado = Sessao.SessaoUsuario.getTrabalhador();
                    mostrarAlerta("Sucesso", "Login de trabalhador realizado com sucesso!");
                    tela_inicio2();
                    return;
                }
            }

            mostrarAlerta("Erro", "Email ou senha inválidos!");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao tentar logar: " + e.getMessage());
        }
    }

    private void abrirTelaInicialDinamicamente(Usuario usuario) {
        try {
            String tipo = usuario.getTipo();
            String fxmlPath;

            if ("Freelancer".equalsIgnoreCase(tipo) || "Meio periodo".equalsIgnoreCase(tipo)) {
                fxmlPath = "/view/Tela_inicial_Trabalhador.fxml";
            } else {
                fxmlPath = "/view/Tela_inicial_Usuario.fxml";
            }

            FXMLLoader loader = springFXMLLoader.load(fxmlPath);
            Parent root = loader.load();
            btnlogin.getScene().setRoot(root);

            FadeTransition ft = new FadeTransition(Duration.millis(250), root);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir a tela inicial.");
        }
    }

    private boolean senhaCorreta(String senhaCriptografada, String senhaDigitada) throws Exception {
        return descriptografar(senhaCriptografada).equals(senhaDigitada);
    }

    private String descriptografar(String valorCriptografado) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(CHAVE_AES, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(valorCriptografado);
        return new String(cipher.doFinal(decoded));
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void telaCadastro() {
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
            mostrarAlerta("Erro", "Não foi possível abrir a tela de cadastro.");
        }
    }

    @FXML
    private void tela_inicio1() {
        try {
            FXMLLoader loader = springFXMLLoader.load("/view/Tela_inicial_Usuario.fxml");
            Parent root = loader.load();
            btnlogin.getScene().setRoot(root);
            FadeTransition ft = new FadeTransition(Duration.millis(250), root);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir a tela do usuário.");
        }
    }

    @FXML
    private void tela_inicio2() {
        try {
            FXMLLoader loader = springFXMLLoader.load("/view/Tela_inicial_Trabalhador.fxml");
            Parent root = loader.load();
            btnlogin.getScene().setRoot(root);
            FadeTransition ft = new FadeTransition(Duration.millis(250), root);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir a tela do trabalhador.");
        }
    }
}
