package org.projeto.javafxmaven.Config;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class SpringFXMLLoader {

    private final ApplicationContext context;

    public SpringFXMLLoader(ApplicationContext context) {
        this.context = context;
    }

    public FXMLLoader load(String fxmlPath) {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalArgumentException("FXML não encontrado: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                return context.getBean(clazz);
            }
        });
        return loader;
    }
}
