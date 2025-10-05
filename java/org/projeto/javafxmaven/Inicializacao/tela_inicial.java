package org.projeto.javafxmaven.Inicializacao;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.projeto.javafxmaven.Config.SpringConfig;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class tela_inicial extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Inicializa o contexto Spring
        springContext = new SpringApplicationBuilder(SpringConfig.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Usa o contexto Spring para o FXMLLoader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Tela_login.fxml"));
        loader.setControllerFactory(springContext::getBean);

        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Tech System");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Fecha o contexto Spring ao sair
        springContext.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
