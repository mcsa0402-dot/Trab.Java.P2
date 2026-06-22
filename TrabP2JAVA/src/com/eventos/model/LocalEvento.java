package com.eventos.model;

import java.time.LocalDateTime;

public class LocalEvento {

    private Long          id;
    private String        nome;
    private String        endereco;
    private int           capacidade;
    private String        descricao;
    private LocalDateTime criadoEm;

    public LocalEvento() {}

    public LocalEvento(Long id, String nome, String endereco,
                       int capacidade, String descricao, LocalDateTime criadoEm) {
        this.id         = id;
        this.nome       = nome;
        this.endereco   = endereco;
        this.capacidade = capacidade;
        this.descricao  = descricao;
        this.criadoEm   = criadoEm;
    }

    public Long          getId()                    { return id; }
    public void          setId(Long v)              { this.id = v; }
    public String        getNome()                  { return nome; }
    public void          setNome(String v)          { this.nome = v; }
    public String        getEndereco()              { return endereco; }
    public void          setEndereco(String v)      { this.endereco = v; }
    public int           getCapacidade()            { return capacidade; }
    public void          setCapacidade(int v)       { this.capacidade = v; }
    public String        getDescricao()             { return descricao; }
    public void          setDescricao(String v)     { this.descricao = v; }
    public LocalDateTime getCriadoEm()              { return criadoEm; }
    public void          setCriadoEm(LocalDateTime v){ this.criadoEm = v; }

    @Override
    public String toString() { return nome + " (cap. " + capacidade + ")"; }
}