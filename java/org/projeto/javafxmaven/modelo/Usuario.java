package org.projeto.javafxmaven.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario extends Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(unique = true, nullable = false)
    private String email;

    @NotEmpty
    @Column(nullable = false)
    private String senha;

    @NotEmpty
    @Column(nullable = false)
    private String nome;

    @NotEmpty
    @Column(nullable = false)
    private String tipo;

    @NotEmpty
    @Column(nullable = false)
    private String cidade;

    @NotEmpty
    @Column(nullable = false)
    private String uf;

    @NotEmpty
    @Column(nullable = false)
    private String cpf;

    @NotNull
    @Temporal(TemporalType.DATE) // <- IMPORTANTE!
    @Column(nullable = false)
    private Date datanasc;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDatanasc() {
        return datanasc;
    }

    public void setDatanasc(Date datanasc) {
        this.datanasc = datanasc;
    }
}
