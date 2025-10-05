package org.projeto.javafxmaven.Controles;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.projeto.javafxmaven.Config.SpringFXMLLoader;
import org.projeto.javafxmaven.modelo.Trabalhador;
import org.projeto.javafxmaven.modelo.Usuario;
import org.projeto.javafxmaven.repository.PessoaRepository;
import org.projeto.javafxmaven.repository.TrabalhadorRepository;
import org.projeto.javafxmaven.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

@Component
public class Tela_cadastro_Pessoa implements Initializable {

    @FXML
    private Label txtcurriculo;
    @FXML
    private Button btncurriculo;
    @FXML
    private Button btnMostrarSenha;
    private boolean senhaVisivel = false;

    private ImageView olhoFechado;
    private ImageView olhoAberto;

    @FXML
    private Button btncadastrar;
    @FXML
    private Button btnlogin;
    @FXML
    private Button btnpessoajuridica;
    @FXML
    private CheckBox chcktermosdeuso;
    @FXML
    private ComboBox<String> cmbUF;
    @FXML
    private ComboBox<String> cmbescolha;
    @FXML
    private DatePicker dtpkdatanasc;
    @FXML
    private TextField pswconfsenha;
    @FXML
    private PasswordField pswsenha;
    @FXML
    private TextField txtcidade;
    @FXML
    private TextField txtcpf;
    @FXML
    private TextField txtemail;
    @FXML
    private TextField txtnome;

    private byte[] curriculoSelecionado;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TrabalhadorRepository trabalhadorRepository;

    private File arquivoCurriculo;

    private boolean emailJaCadastrado(String email) {
        boolean existeEmPessoa = pessoaRepository.findByEmail(email).isPresent();
        boolean existeEmTrabalhador = trabalhadorRepository.findByEmail(email).isPresent();
        return existeEmPessoa || existeEmTrabalhador;
    }

    private boolean cpfJaCadastrado(String cpf) {
        boolean existeEmPessoa = usuarioRepository.findByCpf(cpf).isPresent();
        boolean existeEmTrabalhador = trabalhadorRepository.findByCpf(cpf).isPresent();
        return existeEmPessoa || existeEmTrabalhador;
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
    private void telapessoajuridica() {
        try {
            FXMLLoader loader = springFXMLLoader.load("/view/Tela_cadastro_Juridico.fxml");
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

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void aplicarMascaraCPF(KeyEvent event) {
        String texto = txtcpf.getText().replaceAll("[^0-9]", "");
        if (texto.length() > 11) texto = texto.substring(0, 11);

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            if (i == 3 || i == 6) formatted.append(".");
            else if (i == 9) formatted.append("-");
            formatted.append(texto.charAt(i));
        }
        txtcpf.setText(formatted.toString());
        txtcpf.positionCaret(formatted.length());
    }
    @FXML
    private void aplicarMascaraData(KeyEvent event) {
        String texto = dtpkdatanasc.getEditor().getText().replaceAll("[^0-9]", "");
        if (texto.length() > 8) texto = texto.substring(0, 8);

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            if (i == 2 || i == 4) formatted.append('/');
            formatted.append(texto.charAt(i));
        }

        dtpkdatanasc.getEditor().setText(formatted.toString());
        dtpkdatanasc.getEditor().positionCaret(formatted.length());
    }

    @FXML
    private void selecionarCurriculo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Currículo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Documentos Word", "*.docx", "*.doc")
        );
        File arquivo = fileChooser.showOpenDialog(null);
        if (arquivo != null) {
            try {
                curriculoSelecionado = Files.readAllBytes(arquivo.toPath());
                arquivoCurriculo = arquivo; // <-- Atribuir o arquivo aqui
                txtcurriculo.setText(arquivo.getName());
                mostrarAlerta("Sucesso", "Currículo selecionado com sucesso.");
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao carregar o currículo.");
            }
        }
    }

    private boolean isCPFValido(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma1 = 0, soma2 = 0;
            for (int i = 0; i < 9; i++) {
                int digito = Character.getNumericValue(cpf.charAt(i));
                soma1 += digito * (10 - i);
                soma2 += digito * (11 - i);
            }
            int dig1 = 11 - (soma1 % 11);
            if (dig1 >= 10) dig1 = 0;

            soma2 += dig1 * 2;
            int dig2 = 11 - (soma2 % 11);
            if (dig2 >= 10) dig2 = 0;

            return dig1 == Character.getNumericValue(cpf.charAt(9)) &&
                    dig2 == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isMaiorDeIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) return false;
        LocalDate hoje = LocalDate.now();
        return !dataNascimento.plusYears(18).isAfter(hoje);
    }

    @FXML
    private void cadCliente() {
        String escolha = cmbescolha.getValue();

        if ("Escolha".equals(escolha)) {
            mostrarAlerta("Erro", "Selecione um tipo de usuário.");
            return;
        }

        String nome = txtnome.getText();
        String email = txtemail.getText();
        String cpf = txtcpf.getText();
        LocalDate dataNasc = dtpkdatanasc.getValue();
        String cidade = txtcidade.getText();
        String uf = cmbUF.getValue();
        String senha = pswsenha.getText();
        String confirmarSenha = pswconfsenha.getText();

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || dataNasc == null ||
                cidade.isEmpty() || uf == null || senha.isEmpty() || confirmarSenha.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos!");
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

        if (!isMaiorDeIdade(dataNasc)) {
            mostrarAlerta("Erro", "Você precisa ter pelo menos 18 anos.");
            return;
        }

        if (!chcktermosdeuso.isSelected()) {
            mostrarAlerta("Erro", "Aceite os termos de uso para continuar.");
            return;
        }

        // 🔒 Verifica se email ou CPF já estão cadastrados
        if (emailJaCadastrado(email)) {
            mostrarAlerta("Erro", "E-mail já cadastrado. Tente outro.");
            return;
        }
        if (cpfJaCadastrado(cpf)) {
            mostrarAlerta("Erro", "CPF já cadastrado. Tente outro.");
            return;
        }

        try {
            String senhaCriptografada = CriptografiaAES.criptografar(senha);

            if ("Comum".equals(escolha)) {
                Usuario usuario = new Usuario();
                usuario.setNome(nome);
                usuario.setEmail(email);
                usuario.setCpf(cpf);
                usuario.setUf(uf);
                usuario.setDatanasc(java.sql.Date.valueOf(dataNasc));
                usuario.setCidade(cidade);
                usuario.setSenha(senhaCriptografada);
                usuario.setTipo("Comum");

                pessoaRepository.save(usuario);
            } else {
                if (curriculoSelecionado == null) {
                    mostrarAlerta("Erro", "Selecione um currículo antes de finalizar o cadastro.");
                    return;
                }

                // 🗂️ Verifica tamanho do arquivo (5MB)
                final long TAMANHO_MAXIMO_CURRICULO = 5 * 1024 * 1024; // 5MB
                if (curriculoSelecionado.length > TAMANHO_MAXIMO_CURRICULO) {
                    mostrarAlerta("Erro", "O currículo excede o tamanho máximo permitido (5 MB).");
                    return;
                }

                Trabalhador trabalhador = new Trabalhador();
                trabalhador.setNome(nome);
                trabalhador.setEmail(email);
                trabalhador.setCpf(cpf);
                trabalhador.setUf(uf);
                trabalhador.setDatanasc(java.sql.Date.valueOf(dataNasc));
                trabalhador.setCidade(cidade);
                trabalhador.setSenha(senhaCriptografada);
                trabalhador.setCurriculo(curriculoSelecionado);
                trabalhador.setNomeCurriculo(arquivoCurriculo.getName());
                if("Freelancer".equals(escolha)){
                    trabalhador.setTipo("Freelancer");
                }
                else if("Meio periodo".equals(escolha)){
                    trabalhador.setTipo("Meio periodo");
                }

                trabalhadorRepository.save(trabalhador);
            }

            mostrarAlerta("Sucesso", "Cadastro realizado com sucesso!");
            telalogin();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao cadastrar: " + e.getMessage());
        }
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbescolha.getItems().addAll("Escolha", "Comum", "Freelancer", "Meio periodo");
        cmbUF.getItems().addAll("AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
                "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

        cmbescolha.setValue("Escolha");
        txtcurriculo.setVisible(false);
        btncurriculo.setVisible(false);

        cmbescolha.setOnAction(event -> {
            boolean mostrar = "Freelancer".equals(cmbescolha.getValue()) || "Meio periodo".equals(cmbescolha.getValue());
            txtcurriculo.setVisible(mostrar);
            btncurriculo.setVisible(mostrar);
        });


        txtcpf.setOnKeyReleased(this::aplicarMascaraCPF);

        olhoFechado = new ImageView(new Image(getClass().getResourceAsStream("/icones/olho_fechado.png")));
        olhoAberto = new ImageView(new Image(getClass().getResourceAsStream("/icones/olho_aberto.png")));

        olhoFechado.setFitWidth(20);
        olhoFechado.setFitHeight(20);
        olhoAberto.setFitWidth(20);
        olhoAberto.setFitHeight(20);

        btnMostrarSenha.setGraphic(olhoFechado);
        btnMostrarSenha.setOnAction(event -> alternarImagem());



        txtcpf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !isCPFValido(txtcpf.getText())) {
                mostrarAlerta("CPF inválido", "Digite um CPF válido!");
                txtcpf.setText("");
            }
        });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        dtpkdatanasc.setPromptText("dd/MM/yyyy");
        dtpkdatanasc.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) return null;
                try {
                    return LocalDate.parse(string, formatter);
                } catch (Exception e) {
                    mostrarAlerta("Erro", "Data inválida! Use o formato dd/MM/yyyy.");
                    return null;
                }
            }
        });

// Aplica máscara enquanto digita
        dtpkdatanasc.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, this::aplicarMascaraData);

// Detecta quando o usuário termina de digitar e sai do campo
        dtpkdatanasc.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                String texto = dtpkdatanasc.getEditor().getText();
                if (!texto.isEmpty()) {
                    try {
                        LocalDate data = LocalDate.parse(texto, formatter);
                        dtpkdatanasc.setValue(data);
                    } catch (Exception e) {
                        mostrarAlerta("Erro", "Data inválida! Use o formato dd/MM/yyyy.");
                        dtpkdatanasc.setValue(null);
                    }
                }
            }
        });
    }

    private void alternarImagem() {
        senhaVisivel = !senhaVisivel;
        btnMostrarSenha.setGraphic(senhaVisivel ? olhoAberto : olhoFechado);
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
