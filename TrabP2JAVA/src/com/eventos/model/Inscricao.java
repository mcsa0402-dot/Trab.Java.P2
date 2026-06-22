package com.eventos.model;

import java.time.LocalDateTime;

public class Inscricao {

    public enum Status { CONFIRMADA, CANCELADA, LISTA_ESPERA }

    private Long          id;
    private Evento        evento;
    private Usuario       usuario;
    private LocalDateTime dataInscricao;
    private Status        status;
    private String        observacao;

    public Inscricao() {}

    public Long          getId()                      { return id; }
    public void          setId(Long v)                { this.id = v; }

    public Evento        getEvento()                  { return evento; }
    public void          setEvento(Evento v)          { this.evento = v; }

    public Usuario       getUsuario()                 { return usuario; }
    public void          setUsuario(Usuario v)        { this.usuario = v; }

    public LocalDateTime getDataInscricao()           { return dataInscricao; }
    public void          setDataInscricao(LocalDateTime v){ this.dataInscricao = v; }

    public Status        getStatus()                  { return status; }
    public void          setStatus(Status v)          { this.status = v; }

    public String        getObservacao()              { return observacao; }
    public void          setObservacao(String v)      { this.observacao = v; }
}