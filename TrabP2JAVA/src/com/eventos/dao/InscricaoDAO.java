package com.eventos.dao;

import com.eventos.model.Inscricao;
import com.eventos.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InscricaoDAO {

    private final EventoDAO  eventoDAO  = new EventoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── CREATE ──────────────────────────────────────────────────
    public Inscricao criar(Inscricao i) throws SQLException {
        String sql = "INSERT INTO inscricao (evento_id, usuario_id, status, observacao) VALUES (?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, i.getEvento().getId());
            ps.setLong(2, i.getUsuario().getId());
            ps.setString(3, i.getStatus().name());
            ps.setString(4, i.getObservacao());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) i.setId(rs.getLong(1));
            }
        }
        return i;
    }

    // ── READ (by ID) ─────────────────────────────────────────────
    public Optional<Inscricao> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM inscricao WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    // ── READ (by evento) ─────────────────────────────────────────
    public List<Inscricao> listarPorEvento(Long eventoId) throws SQLException {
        List<Inscricao> lista = new ArrayList<>();
        String sql = "SELECT * FROM inscricao WHERE evento_id = ? ORDER BY data_inscricao";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, eventoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── READ (by usuario) ────────────────────────────────────────
    public List<Inscricao> listarPorUsuario(Long usuarioId) throws SQLException {
        List<Inscricao> lista = new ArrayList<>();
        String sql = "SELECT * FROM inscricao WHERE usuario_id = ? ORDER BY data_inscricao DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── Verifica inscrição duplicada ─────────────────────────────
    public boolean existeInscricao(Long eventoId, Long usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscricao WHERE evento_id=? AND usuario_id=? AND status != 'CANCELADA'";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, eventoId);
            ps.setLong(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ── UPDATE (status) ──────────────────────────────────────────
    public void atualizarStatus(Long id, Inscricao.Status status) throws SQLException {
        String sql = "UPDATE inscricao SET status = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    // ── DELETE ──────────────────────────────────────────────────
    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM inscricao WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // ── MAPPER ──────────────────────────────────────────────────
    private Inscricao mapear(ResultSet rs) throws SQLException {
        Inscricao i = new Inscricao();
        i.setId(rs.getLong("id"));
        i.setStatus(Inscricao.Status.valueOf(rs.getString("status")));
        i.setObservacao(rs.getString("observacao"));
        Timestamp ts = rs.getTimestamp("data_inscricao");
        if (ts != null) i.setDataInscricao(ts.toLocalDateTime());

        eventoDAO.buscarPorId(rs.getLong("evento_id")).ifPresent(i::setEvento);
        usuarioDAO.buscarPorId(rs.getLong("usuario_id")).ifPresent(i::setUsuario);
        return i;
    }
}