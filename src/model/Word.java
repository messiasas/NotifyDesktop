package model;

import java.time.LocalDateTime;

public class Word {

    private int id;
    private String termo;
    private String traducao;
    private String exemploFrase; // opcional
    private LocalDateTime dataCriacao;

    public Word() {
    }

    public Word(String termo, String traducao, String exemploFrase) {
        this.termo = termo;
        this.traducao = traducao;
        this.exemploFrase = exemploFrase;
        this.dataCriacao = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTermo() {
        return termo;
    }

    public void setTermo(String termo) {
        this.termo = termo;
    }

    public String getTraducao() {
        return traducao;
    }

    public void setTraducao(String traducao) {
        this.traducao = traducao;
    }

    public String getExemploFrase() {
        return exemploFrase;
    }

    public void setExemploFrase(String exemploFrase) {
        this.exemploFrase = exemploFrase;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public String toString() {
        return termo + " -> " + traducao;
    }
}
