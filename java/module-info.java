module org.projeto.javafxmaven {
    requires javafx.controls;
    requires javafx.fxml;

    requires spring.jdbc;
    requires spring.orm;
    requires spring.context;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires jakarta.validation;
    requires spring.web;
    requires spring.data.commons;
    requires java.desktop;
    requires spring.data.jpa;
    requires spring.beans;
    requires java.persistence;
    requires spring.tx;
    requires spring.core;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires jakarta.annotation;

    // JavaFX - abrir os pacotes que precisam do FXMLLoader e Spring
    opens org.projeto.javafxmaven.Controles to javafx.fxml, spring.core;
    opens org.projeto.javafxmaven.Inicializacao to javafx.fxml;
    opens org.projeto.javafxmaven.Config to spring.core, spring.beans, spring.context;

    // Para ConnectionFactory: exportar e abrir para Spring
    exports org.projeto.javafxmaven.ConnectionFactory to spring.beans;
    opens org.projeto.javafxmaven.ConnectionFactory to spring.core, spring.beans, spring.context;

    // Entidades JPA - abrir para Hibernate ORM, Spring Core, Validação e para todos os módulos (inclui unnamed)
    opens org.projeto.javafxmaven.modelo;

    // Exports públicos do seu módulo
    exports org.projeto.javafxmaven.Config;
    exports org.projeto.javafxmaven.Controles;
    exports org.projeto.javafxmaven.Inicializacao;
}
