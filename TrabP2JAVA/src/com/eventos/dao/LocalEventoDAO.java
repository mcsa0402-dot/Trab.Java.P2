package com.eventos.dao;

import com.eventos.model.LocalEvento;
import com.eventos.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocalEventoDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public LocalEvento criar(LocalEvento l) throws SQLException {
        String sql = "INSERT INTO local_evento (nome, endereco, capacidade, descricao) VALUES (?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getNome());
            ps.setString(2, l.getEndereco());
            ps.setInt(3, l.getCapacidade());
            ps.setString(4, l.getDescricao());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) l.setId(rs.getLong(1));
            }
        }
        return l;
    }

    public Optional<LocalEvento> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM local_evento WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    public List<LocalEvento> listarTodos() throws SQLException {
        List<LocalEvento> lista = new ArrayList<>();
        String sql = "SELECT * FROM local_evento ORDER BY nome";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void atualizar(LocalEvento l) throws SQLException {
        String sql = "UPDATE local_evento SET nome=?, endereco=?, capacidade=?, descricao=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, l.getNome());
            ps.setString(2, l.getEndereco());
            ps.setInt(3, l.getCapacidade());
            ps.setString(4, l.getDescricao());
            ps.setLong(5, l.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM local_evento WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private LocalEvento mapear(ResultSet rs) throws SQLException {
        LocalEvento l = new LocalEvento();
        l.setId(rs.getLong("id"));
        l.setNome(rs.getString("nome"));
        l.setEndereco(rs.getString("endereco"));
        l.setCapacidade(rs.getInt("capacidade"));
        l.setDescricao(rs.getString("descricao"));
        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) l.setCriadoEm(ts.toLocalDateTime());
        return l;
    }
}