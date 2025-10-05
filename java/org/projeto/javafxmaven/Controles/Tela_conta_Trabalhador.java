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
import org.projeto.javafxmaven.Config.SpringFXMLLoader;
import org.projeto.javafxmaven.Config.TrabalhadorService;
import org.projeto.javafxmaven.Config.UsuarioService;
import org.projeto.javafxmaven.modelo.Trabalhador;
import org.projeto.javafxmaven.repository.JuridicoRepository;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.ResourceBundle;

@Component
public class Tela_conta_Trabalhador implements Initializable {

    @FXML private Button btndownload, btncurriculo, btnaccount, btncancelar, btnclients, btndashboard, btngeko, btnsair, btnsalvar, btnservices, btnspeciality, btnstatistichs;
    @FXML private MenuButton cmbtipo;
    @FXML private DatePicker dtkdatansac;
    @FXML private PasswordField pswsenha;
    @FXML private TextField txtcidade, txtcpf, txtemail, txtname, txtnomeusuario;
    @FXML private ComboBox<String> cmbUF;

    private byte[] curriculoSelecionado;
    private File arquivoCurriculo;
    private static final byte[] CHAVE_AES = "2312024202526123".getBytes();

    @Autowired private SpringFXMLLoader springFXMLLoader;
    @Autowired private UsuarioService usuarioService;
    @Autowired private TrabalhadorRepository trabalhadorRepository;
    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private JuridicoRepository juridicoRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        adicionarOpcoesTipo();
        cmbUF.getItems().addAll("AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

        try {
            carregarDadosUsuario();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        txtcpf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !isCPFValido(txtcpf.getText())) {
                mostrarAlerta("CPF inválido", "Digite um CPF válido!");
                txtcpf.setText("");
            }
        });
        txtcpf.setOnKeyReleased(this::aplicarMascaraCPF);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        dtkdatansac.setPromptText("dd/MM/yyyy");
        dtkdatansac.setConverter(new StringConverter<LocalDate>() {
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
        dtkdatansac.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, this::aplicarMascaraData);

// Detecta quando o usuário termina de digitar e sai do campo
        dtkdatansac.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                String texto =  dtkdatansac.getEditor().getText();
                if (!texto.isEmpty()) {
                    try {
                        LocalDate data = LocalDate.parse(texto, formatter);
                        dtkdatansac.setValue(data);
                    } catch (Exception e) {
                        mostrarAlerta("Erro", "Data inválida! Use o formato dd/MM/yyyy.");
                        dtkdatansac.setValue(null);
                    }
                }
            }
        });
    }

    private void adicionarOpcoesTipo() {
        String[] tipos = {"Freelancer", "Meio periodo"};

        for (String tipo : tipos) {
            MenuItem item = new MenuItem(tipo);
            item.setOnAction(e -> cmbtipo.setText(tipo));
            cmbtipo.getItems().add(item);
        }
    }

    private boolean cpfJaCadastrado(String cpf) {
        return usuarioRepository.findByCpf(cpf).isPresent() || trabalhadorRepository.findByCpf(cpf).isPresent();
    }

    public String validarGmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$") ? "OK" : "O e-mail deve ser um Gmail válido, exemplo: usuario@gmail.com.";
    }

    private boolean emailJaCadastrado(String email) {
        return pessoaRepository.findByEmail(email).isPresent() || trabalhadorRepository.findByEmail(email).isPresent() || juridicoRepository.findByEmail(email).isPresent();
    }
    private void carregarDadosUsuario() throws Exception {
        var usuario = usuarioService.getUsuarioLogado();
        if (usuario instanceof Trabalhador trabalhador) {
            txtname.setText(trabalhador.getNome());
            txtemail.setText(trabalhador.getEmail());
            txtcpf.setText(trabalhador.getCpf());

            Date dataNasc = trabalhador.getDatanasc();
            if (dataNasc != null) {
                dtkdatansac.setValue(dataNasc.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }

            cmbUF.setValue(trabalhador.getUf());
            txtcidade.setText(trabalhador.getCidade());
            pswsenha.setText(descriptografar(trabalhador.getSenha()));
            cmbtipo.setText(trabalhador.getTipo());
            btncurriculo.setText(trabalhador.getNomeCurriculo());
        } else {
            mostrarAlerta("Erro", "O usuário logado não é do tipo Trabalhador.");
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
        String texto = dtkdatansac.getEditor().getText().replaceAll("[^0-9]", "");
        if (texto.length() > 8) texto = texto.substring(0, 8);

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            if (i == 2 || i == 4) formatted.append('/');
            formatted.append(texto.charAt(i));
        }
        dtkdatansac.getEditor().setText(formatted.toString());
        dtkdatansac.getEditor().positionCaret(formatted.length());
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
                arquivoCurriculo = arquivo;
                btncurriculo.setText(arquivo.getName());
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

            return dig1 == Character.getNumericValue(cpf.charAt(9)) && dig2 == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isMaiorDeIdade(LocalDate dataNascimento) {
        return dataNascimento != null && !dataNascimento.plusYears(18).isAfter(LocalDate.now());
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
        String cpf = txtcpf.getText();
        LocalDate dataNascLocal = dtkdatansac.getValue();
        String cidade = txtcidade.getText();
        String uf = cmbUF.getValue();
        String tipo = cmbtipo.getText();
        String senha = pswsenha.getText();

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || dataNascLocal == null || cidade.isEmpty() || uf == null || tipo == null || senha.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos!");
            return;
        }

        String validacaoEmail = validarGmail(email);
        if (!"OK".equals(validacaoEmail)) {
            mostrarAlerta("Erro", validacaoEmail);
            return;
        }

        if (!isMaiorDeIdade(dataNascLocal)) {
            mostrarAlerta("Erro", "Você precisa ter pelo menos 18 anos.");
            return;
        }

        var usuario = usuarioService.getUsuarioLogado();
        if (usuario instanceof Trabalhador trabalhador) {
            if (!email.equals(trabalhador.getEmail()) && emailJaCadastrado(email)) {
                mostrarAlerta("Erro", "E-mail já cadastrado. Tente outro.");
                return;
            }

            if (!cpf.equals(trabalhador.getCpf()) && cpfJaCadastrado(cpf)) {
                mostrarAlerta("Erro", "CPF já cadastrado. Tente outro.");
                return;
            }

            trabalhador.setNome(nome);
            trabalhador.setEmail(email);
            trabalhador.setCpf(cpf);
            trabalhador.setUf(uf);
            trabalhador.setCidade(cidade);
            trabalhador.setSenha(CriptografiaAES.criptografar(senha));
            trabalhador.setTipo(tipo);

            if (curriculoSelecionado != null && curriculoSelecionado.length <= 5 * 1024 * 1024) {
                trabalhador.setCurriculo(curriculoSelecionado);
                if (arquivoCurriculo != null)
                    trabalhador.setNomeCurriculo(arquivoCurriculo.getName());
            }

            trabalhador.setDatanasc(Date.from(dataNascLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            if (usuarioService.salvar(trabalhador)) {
                mostrarAlerta("Sucesso", "Seus dados foram atualizados.");
            } else {
                mostrarAlerta("Erro", "Ocorreu um erro ao salvar os dados.");
            }
        } else {
            mostrarAlerta("Erro", "Usuário logado não é um Trabalhador.");
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

    @FXML private void tela_publicar() { carregarTela("/view/Tela_divulgar.fxml", btnspeciality); }
    @FXML private void tela_dashborad() { carregarTela("/view/Tela_inicial_Trabalhador.fxml", btndashboard); }
    @FXML private void tela_statisticas() { carregarTela("/view/Tela_estatistica_trabalhador.fxml", btnstatistichs); }
    @FXML private void tela_chat() { carregarTela("/view/Tela_chat_Trabalhador.fxml", btnclients); }
    @FXML private void tela_services() { carregarTela("/view/Tela_servicos.fxml", btnservices); }
}