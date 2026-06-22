package com.eventos.model;

import java.time.LocalDateTime;

public class Evento {

    public enum Status { ATIVO, CANCELADO, ENCERRADO }

    private Long          id;
    private String        titulo;
    private String        descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private LocalEvento   local;
    private int           capacidade;
    private String        categoria;
    private Usuario       organizador;
    private Status        status;
    private LocalDateTime criadoEm;

    public Evento() {}

    // ── Getters & Setters ──────────────────────────────────────
    public Long          getId()                     { return id; }
    public void          setId(Long v)               { this.id = v; }

    public String        getTitulo()                 { return titulo; }
    public void          setTitulo(String v)         { this.titulo = v; }

    public String        getDescricao()              { return descricao; }
    public void          setDescricao(String v)      { this.descricao = v; }

    public LocalDateTime getDataInicio()             { return dataInicio; }
    public void          setDataInicio(LocalDateTime v){ this.dataInicio = v; }

    public LocalDateTime getDataFim()                { return dataFim; }
    public void          setDataFim(LocalDateTime v) { this.dataFim = v; }

    public LocalEvento   getLocal()                  { return local; }
    public void          setLocal(LocalEvento v)     { this.local = v; }

    public int           getCapacidade()             { return capacidade; }
    public void          setCapacidade(int v)        { this.capacidade = v; }

    public String        getCategoria()              { return categoria; }
    public void          setCategoria(String v)      { this.categoria = v; }

    public Usuario       getOrganizador()            { return organizador; }
    public void          setOrganizador(Usuario v)   { this.organizador = v; }

    public Status        getStatus()                 { return status; }
    public void          setStatus(Status v)         { this.status = v; }

    public LocalDateTime getCriadoEm()               { return criadoEm; }
    public void          setCriadoEm(LocalDateTime v){ this.criadoEm = v; }

    @Override
    public String toString() { return titulo; }
}