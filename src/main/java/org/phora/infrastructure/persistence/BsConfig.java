package org.phora.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;

public class BsConfig {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/inventario";
            String user = "admin";
            String pass = "proyectoInv2306";

            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}