package org.projeto.javafxmaven.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Trabalhador extends Usuario {

    @NotEmpty
    @Lob
    @Column(name = "curriculo", columnDefinition = "LONGBLOB") // ou MEDIUMBLOB
    private byte[] curriculo;

    @NotEmpty
    private String nomeCurriculo;

    public Trabalhador() {
        super(); // chama o construtor da superclasse
    }

    public Trabalhador(String cpf, byte[] curriculo, String nomeCurriculo) {
        this.curriculo = curriculo;
        this.nomeCurriculo = nomeCurriculo;
    }

    // Getters e Setters

    public byte[] getCurriculo() {
        return curriculo;
    }

    public void setCurriculo(byte[] curriculo) {
        this.curriculo = curriculo;
    }

    public String getNomeCurriculo() {
        return nomeCurriculo;
    }

    public void setNomeCurriculo(String nomeCurriculo) {
        this.nomeCurriculo = nomeCurriculo;
    }
}
