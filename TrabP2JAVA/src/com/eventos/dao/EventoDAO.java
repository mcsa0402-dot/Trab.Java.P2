package com.eventos.dao;

import com.eventos.model.Evento;
import com.eventos.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventoDAO {

    private final LocalEventoDAO localDAO = new LocalEventoDAO();
    private final UsuarioDAO     usuarioDAO = new UsuarioDAO();

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── CREATE ──────────────────────────────────────────────────
    public Evento criar(Evento e) throws SQLException {
        String sql = "INSERT INTO evento (titulo, descricao, data_inicio, data_fim, local_id, capacidade, categoria, organizador_id, status) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getTitulo());
            ps.setString(2, e.getDescricao());
            ps.setTimestamp(3, Timestamp.valueOf(e.getDataInicio()));
            ps.setTimestamp(4, Timestamp.valueOf(e.getDataFim()));
            ps.setLong(5, e.getLocal().getId());
            ps.setInt(6, e.getCapacidade());
            ps.setString(7, e.getCategoria());
            ps.setLong(8, e.getOrganizador().getId());
            ps.setString(9, e.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getLong(1));
            }
        }
        return e;
    }

    // ── READ (by ID) ─────────────────────────────────────────────
    public Optional<Evento> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM evento WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    // ── READ (all) ───────────────────────────────────────────────
    public List<Evento> listarTodos() throws SQLException {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM evento ORDER BY data_inicio";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── READ (ativos) ────────────────────────────────────────────
    public List<Evento> listarAtivos() throws SQLException {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM evento WHERE status = 'ATIVO' ORDER BY data_inicio";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── Conta inscrições confirmadas de um evento ─────────────────
    public int contarInscritos(Long eventoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscricao WHERE evento_id = ? AND status = 'CONFIRMADA'";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, eventoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // ── Verifica conflito de horário no mesmo local ───────────────
    /**
     * Regra de negócio 2: verifica se outro evento já ocupa o mesmo local
     * no intervalo de datas solicitado (exclui o próprio evento em edição).
     */
    public boolean existeConflitoLocal(Long localId, java.time.LocalDateTime inicio,
                                       java.time.LocalDateTime fim, Long ignorarEventoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM evento " +
                     "WHERE local_id = ? AND status = 'ATIVO' " +
                     "AND id != ? " +
                     "AND NOT (data_fim <= ? OR data_inicio >= ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, localId);
            ps.setLong(2, ignorarEventoId == null ? -1 : ignorarEventoId);
            ps.setTimestamp(3, Timestamp.valueOf(inicio));
            ps.setTimestamp(4, Timestamp.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ── UPDATE ──────────────────────────────────────────────────
    public void atualizar(Evento e) throws SQLException {
        String sql = "UPDATE evento SET titulo=?, descricao=?, data_inicio=?, data_fim=?, local_id=?, capacidade=?, categoria=?, organizador_id=?, status=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, e.getTitulo());
            ps.setString(2, e.getDescricao());
            ps.setTimestamp(3, Timestamp.valueOf(e.getDataInicio()));
            ps.setTimestamp(4, Timestamp.valueOf(e.getDataFim()));
            ps.setLong(5, e.getLocal().getId());
            ps.setInt(6, e.getCapacidade());
            ps.setString(7, e.getCategoria());
            ps.setLong(8, e.getOrganizador().getId());
            ps.setString(9, e.getStatus().name());
            ps.setLong(10, e.getId());
            ps.executeUpdate();
        }
    }

    // ── DELETE ──────────────────────────────────────────────────
    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM evento WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // ── MAPPER ──────────────────────────────────────────────────
    private Evento mapear(ResultSet rs) throws SQLException {
        Evento e = new Evento();
        e.setId(rs.getLong("id"));
        e.setTitulo(rs.getString("titulo"));
        e.setDescricao(rs.getString("descricao"));
        e.setDataInicio(rs.getTimestamp("data_inicio").toLocalDateTime());
        e.setDataFim(rs.getTimestamp("data_fim").toLocalDateTime());
        e.setCapacidade(rs.getInt("capacidade"));
        e.setCategoria(rs.getString("categoria"));
        e.setStatus(Evento.Status.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) e.setCriadoEm(ts.toLocalDateTime());

        localDAO.buscarPorId(rs.getLong("local_id")).ifPresent(e::setLocal);
        usuarioDAO.buscarPorId(rs.getLong("organizador_id")).ifPresent(e::setOrganizador);
        return e;
    }
}