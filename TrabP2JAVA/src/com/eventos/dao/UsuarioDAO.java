package com.eventos.dao;

import com.eventos.model.Usuario;
import com.eventos.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── CREATE ──────────────────────────────────────────────────
    public Usuario criar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nome, email, senha_hash, tipo, telefone) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenhaHash());
            ps.setString(4, u.getTipo().name());
            ps.setString(5, u.getTelefone());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getLong(1));
            }
        }
        return u;
    }

    // ── READ (by ID) ─────────────────────────────────────────────
    public Optional<Usuario> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    // ── READ (by email) ──────────────────────────────────────────
    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    // ── READ (all) ───────────────────────────────────────────────
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nome";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── UPDATE ──────────────────────────────────────────────────
    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET nome=?, email=?, senha_hash=?, tipo=?, telefone=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenhaHash());
            ps.setString(4, u.getTipo().name());
            ps.setString(5, u.getTelefone());
            ps.setLong(6, u.getId());
            ps.executeUpdate();
        }
    }

    // ── DELETE ──────────────────────────────────────────────────
    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // ── MAPPER ──────────────────────────────────────────────────
    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setTipo(Usuario.Tipo.valueOf(rs.getString("tipo")));
        u.setTelefone(rs.getString("telefone"));
        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) u.setCriadoEm(ts.toLocalDateTime());
        return u;
    }
}