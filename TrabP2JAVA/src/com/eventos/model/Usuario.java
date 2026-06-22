package com.eventos.model;

import java.time.LocalDateTime;

public class Usuario {

    public enum Tipo { ORGANIZADOR, VOLUNTARIO, PUBLICO }

    private Long          id;
    private String        nome;
    private String        email;
    private String        senhaHash;
    private Tipo          tipo;
    private String        telefone;
    private LocalDateTime criadoEm;

    public Usuario() {}

    public Usuario(Long id, String nome, String email, String senhaHash,
                   Tipo tipo, String telefone, LocalDateTime criadoEm) {
        this.id        = id;
        this.nome      = nome;
        this.email     = email;
        this.senhaHash = senhaHash;
        this.tipo      = tipo;
        this.telefone  = telefone;
        this.criadoEm  = criadoEm;
    }

    // ── Getters & Setters ──────────────────────────────────────
    public Long          getId()        { return id; }
    public void          setId(Long v)  { this.id = v; }

    public String        getNome()           { return nome; }
    public void          setNome(String v)   { this.nome = v; }

    public String        getEmail()          { return email; }
    public void          setEmail(String v)  { this.email = v; }

    public String        getSenhaHash()            { return senhaHash; }
    public void          setSenhaHash(String v)    { this.senhaHash = v; }

    public Tipo          getTipo()          { return tipo; }
    public void          setTipo(Tipo v)    { this.tipo = v; }

    public String        getTelefone()          { return telefone; }
    public void          setTelefone(String v)  { this.telefone = v; }

    public LocalDateTime getCriadoEm()              { return criadoEm; }
    public void          setCriadoEm(LocalDateTime v){ this.criadoEm = v; }

    @Override
    public String toString() { return nome + " (" + tipo + ")"; }
}