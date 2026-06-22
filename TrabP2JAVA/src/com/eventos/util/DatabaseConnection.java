package com.eventos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton responsável pela conexão com o banco de dados MySQL via JDBC.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/eventos_comunitarios?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "root";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado. Adicione mysql-connector-java ao classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null || isConnectionClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private static boolean isConnectionClosed() {
        try {
            return instance.connection == null || instance.connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        if (instance != null && !isConnectionClosed()) {
            try {
                instance.connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}