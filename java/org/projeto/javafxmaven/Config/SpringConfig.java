package org.projeto.javafxmaven.Config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "org.projeto.javafxmaven.modelo")
@EnableJpaRepositories(basePackages = "org.projeto.javafxmaven.repository")
@ComponentScan(basePackages = "org.projeto.javafxmaven") // garante que todas as classes sejam escaneadas
public class SpringConfig {

    public static void main(String[] args) {
        SpringApplication.run(SpringConfig.class, args);
    }
}
