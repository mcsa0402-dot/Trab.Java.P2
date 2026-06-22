package com.eventos;

import com.eventos.util.DatabaseConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        if (conn != null) {
            System.out.println("Conexão com o banco estabelecida com sucesso!");
        } else {
            System.out.println("Falha na conexão.");
        }
    }
}