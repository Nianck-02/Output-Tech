package org.projeto.javafxmaven.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Juridico extends Pessoa {

    @NotEmpty
    @Column(unique = true, nullable = false)
    private String cnpj;

    @NotEmpty
    @Column(nullable = false)
    private String tipo; // Ex: "trabalhador", "juridico", etc.

    @NotEmpty
    @Column(nullable = false)
    private String cidade;

    @NotEmpty
    @Column(nullable = false)
    private String uf;

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Juridico() {
        super(); // Chama o construtor da superclasse
    }

    public Juridico(String cnpj) {
        this.cnpj = cnpj;
    }

    // Getters e Setters

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
