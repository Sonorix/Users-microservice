/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.users.ms.model;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Administrador
 */
public class Database {
    private Dotenv dotenv;
    
    public Database() {
        try {
            this.dotenv = Dotenv.load();
            validateEnvVariables();
        } catch (Exception e) {
            throw new RuntimeException("Error loading environment variables: " + e.getMessage(), e);
        }
    }
    
    private void validateEnvVariables() {
        if (dotenv == null) {
            throw new RuntimeException("Failed to load .env file");
        }
        
        if (dotenv.get("DB_URL") == null) {
            throw new RuntimeException("Database URL not found in environment variables");
        }
        
        if (dotenv.get("DB_USER") == null) {
            throw new RuntimeException("Database user not found in environment variables");
        }
        
        if (dotenv.get("DB_PASSWORD") == null) {
            throw new RuntimeException("Database password not found in environment variables");
        }
    }
    
    public Connection getConn() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(
                dotenv.get("DB_URL"),
                dotenv.get("DB_USER"),
                dotenv.get("DB_PASSWORD")
            );
        } catch (ClassNotFoundException | SQLException e) {
            throw e;
        }
    }
}
