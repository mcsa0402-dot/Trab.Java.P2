package com.eventos.service;

import com.eventos.dao.EventoDAO;
import com.eventos.dao.InscricaoDAO;
import com.eventos.model.Evento;
import com.eventos.model.Inscricao;
import com.eventos.model.Usuario;

import java.sql.SQLException;
import java.util.List;

/**
 * Service de Eventos.
 * Contém as 3 regras de negócio obrigatórias:
 *  RN-1: Verificação de lotação (capacidade máxima do evento)
 *  RN-2: Conflito de horário/local (mesmo local não pode ter 2 eventos simultâneos)
 *  RN-3: Reserva duplicada (usuário não pode se inscrever 2 vezes no mesmo evento ativo)
 */
public class EventoService {

    private final EventoDAO    eventoDAO    = new EventoDAO();
    private final InscricaoDAO inscricaoDAO = new InscricaoDAO();

    // ── CRUD básico ──────────────────────────────────────────────
    public List<Evento> listarAtivos() throws SQLException {
        return eventoDAO.listarAtivos();
    }

    public List<Evento> listarTodos() throws SQLException {
        return eventoDAO.listarTodos();
    }

    public Evento buscarPorId(Long id) throws SQLException {
        return eventoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + id));
    }

    /**
     * Cria um evento com validação das regras de negócio RN-2.
     */
    public Evento criarEvento(Evento e) throws SQLException {
        validarDatas(e);
        // RN-2: conflito de local
        if (eventoDAO.existeConflitoLocal(e.getLocal().getId(), e.getDataInicio(), e.getDataFim(), null)) {
            throw new IllegalStateException(
                "RN-2: O local '" + e.getLocal().getNome() +
                "' já está reservado nesse horário. Escolha outro local ou horário.");
        }
        return eventoDAO.criar(e);
    }

    /**
     * Atualiza evento com validação RN-2.
     */
    public void atualizarEvento(Evento e) throws SQLException {
        validarDatas(e);
        if (eventoDAO.existeConflitoLocal(e.getLocal().getId(), e.getDataInicio(), e.getDataFim(), e.getId())) {
            throw new IllegalStateException(
                "RN-2: O local '" + e.getLocal().getNome() +
                "' já está reservado nesse horário. Escolha outro local ou horário.");
        }
        eventoDAO.atualizar(e);
    }

    public void cancelarEvento(Long id) throws SQLException {
        Evento e = buscarPorId(id);
        e.setStatus(Evento.Status.CANCELADO);
        eventoDAO.atualizar(e);
    }

    public void deletarEvento(Long id) throws SQLException {
        eventoDAO.deletar(id);
    }

    // ── INSCRIÇÃO ────────────────────────────────────────────────
    /**
     * Inscreve um usuário em um evento aplicando RN-1 e RN-3.
     */
    public Inscricao inscrever(Evento evento, Usuario usuario) throws SQLException {
        // RN-3: reserva duplicada
        if (inscricaoDAO.existeInscricao(evento.getId(), usuario.getId())) {
            throw new IllegalStateException(
                "RN-3: Você já possui uma inscrição ativa neste evento.");
        }

        int vagas      = evento.getCapacidade();
        int inscritos  = eventoDAO.contarInscritos(evento.getId());

        Inscricao.Status status;

        // RN-1: verificação de lotação → lista de espera se lotado
        if (inscritos >= vagas) {
            status = Inscricao.Status.LISTA_ESPERA;
        } else {
            status = Inscricao.Status.CONFIRMADA;
        }

        Inscricao i = new Inscricao();
        i.setEvento(evento);
        i.setUsuario(usuario);
        i.setStatus(status);
        inscricaoDAO.criar(i);

        if (status == Inscricao.Status.LISTA_ESPERA) {
            throw new IllegalStateException(
                "RN-1: Evento lotado! Você foi adicionado à lista de espera.");
        }
        return i;
    }

    public void cancelarInscricao(Long inscricaoId) throws SQLException {
        inscricaoDAO.atualizarStatus(inscricaoId, Inscricao.Status.CANCELADA);
    }

    public List<Inscricao> listarInscricoesPorEvento(Long eventoId) throws SQLException {
        return inscricaoDAO.listarPorEvento(eventoId);
    }

    public List<Inscricao> listarInscricoesPorUsuario(Long usuarioId) throws SQLException {
        return inscricaoDAO.listarPorUsuario(usuarioId);
    }

    public int contarInscritos(Long eventoId) throws SQLException {
        return eventoDAO.contarInscritos(eventoId);
    }

    // ── Validação auxiliar ───────────────────────────────────────
    private void validarDatas(Evento e) {
        if (e.getDataFim().isBefore(e.getDataInicio()) || e.getDataFim().isEqual(e.getDataInicio())) {
            throw new IllegalArgumentException("A data de fim deve ser posterior à data de início.");
        }
    }
}